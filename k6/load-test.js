import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const errorRate = new Rate('errors');
export const options = {
    stages: [
        { duration: '2m', target: 150 },
        { duration: '10m', target: 150 },  
        { duration: '2m', target: 0 },     
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000', 'p(99)<5000'], // Load test: 95% under 2s, 99% under 5s
        http_req_failed: ['rate<0.1'],      // Load test: http errors should be less than 10%
        errors: ['rate<0.05'],              // Load test: custom error rate should be less than 5%
    },
};

const BASE_URL = 'http://localhost:6565';

const testCredentials = {
    username: 'admin',
    password: 'admin123'
};

export function setup() {
    const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify(testCredentials), {
        headers: { 'Content-Type': 'application/json' },
    });

    if (loginRes.status === 200) {
        try {
            const token = JSON.parse(loginRes.body).token;
            console.log('Login successful for load test');

            const headers = {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            };

            const productTypeIds = [];
            for (let i = 0; i < 5; i++) {
                const productTypeData = {
                    name: `Load Test Category ${Date.now()}-${i}`
                };

                const response = http.post(`${BASE_URL}/api/v1/product-types`, JSON.stringify(productTypeData), { headers });
                if (response.status === 201) {
                    const location = response.headers['Location'];
                    if (location) {
                        productTypeIds.push(parseInt(location.split('/').pop()));
                    }
                }
            }

            return {
                token: token,
                productTypeIds: productTypeIds.length > 0 ? productTypeIds : [1]
            };
        } catch (e) {
            console.log('Login response is not valid JSON:', loginRes.body);
            return { token: null, productTypeIds: [1] };
        }
    }

    console.log(`Login failed with status ${loginRes.status}:`, loginRes.body);
    return { token: null, productTypeIds: [1] };
}

export default function (data) {
    const headers = {
        'Content-Type': 'application/json',
    };

    if (data.token) {
        headers['Authorization'] = `Bearer ${data.token}`;
    }

    // Load test scenario distribution
    const scenario = Math.random();

    if (scenario < 0.6) {
        basicReadOperations(headers);
    } else if (scenario < 0.85) {
        basicProductOperations(headers, data);
    } else {
        basicProductTypeOperations(headers);
    }

    sleep(1);
}

function basicReadOperations(headers) {
    let response = http.get(`${BASE_URL}/api/health`, { headers });
    const healthCheck = check(response, {
        'load health check ok': (r) => r.status === 200,
    });
    errorRate.add(!healthCheck);

    response = http.get(`${BASE_URL}/api/v1/products?page=0&size=10`, { headers });
    const productsCheck = check(response, {
        'load products list ok': (r) => r.status === 200,
        'load products response time ok': (r) => r.timings.duration < 2000,
    });
    errorRate.add(!productsCheck);
}

function basicProductOperations(headers, data) {
    if (!data.token) {
        basicReadOperations(headers);
        return;
    }

    const productData = {
        name: `Load-${Date.now()}-${Math.random().toString(36).substr(2, 15)}`,
        description: 'Load test product',
        quantity: Math.floor(Math.random() * 100) + 50,
        lowStockThreshold: 10,
        weight: Math.random() * 5 + 1,
        height: Math.random() * 20 + 5,
        length: Math.random() * 30 + 10,
        productTypeId: data.productTypeIds[Math.floor(Math.random() * data.productTypeIds.length)]
    };

    const response = http.post(`${BASE_URL}/api/v1/products`, JSON.stringify(productData), { headers });
    const createCheck = check(response, {
        'load product creation ok': (r) => r.status === 201 || r.status === 409,
    });
    errorRate.add(!createCheck);

    if (response.status === 201) {
        const location = response.headers['Location'];
        if (location) {
            const productId = location.split('/').pop();

            const deleteResponse = http.del(`${BASE_URL}/api/v1/products/${productId}`, null, { headers });
            const deleteCheck = check(deleteResponse, {
                'load product deletion ok': (r) => r.status === 204,
            });
            errorRate.add(!deleteCheck);
        }
    }
}

function basicProductTypeOperations(headers) {
    if (!headers['Authorization']) {
        basicReadOperations(headers);
        return;
    }

    const response = http.get(`${BASE_URL}/api/v1/product-types?page=0&size=20`, { headers });
    const check_result = check(response, {
        'load product types list ok': (r) => r.status === 200,
    });
    errorRate.add(!check_result);
}

export function teardown(data) {
    console.log('Load test completed');

    if (data.token && data.productTypeIds) {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${data.token}`
        };

        data.productTypeIds.forEach(id => {
            http.del(`${BASE_URL}/api/v1/product-types/${id}`, null, { headers });
        });
    }
}

export function handleSummary(data) {
    return {
        'load-test-report.html': htmlReport(data, {
            title: 'Warehouse Service - Load Test Report'
        }),
        stdout: textSummary(data, { indent: ' ', enableColors: true }),
    };
}



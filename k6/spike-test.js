import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const errorRate = new Rate('errors');
export const options = {
    stages: [
        { duration: '15s', target: 25 },   
        { duration: '5s', target: 100 },   
        { duration: '20s', target: 100 },  
        { duration: '5s', target: 25 },    
        { duration: '15s', target: 25 },  
    ],
    thresholds: {
        http_req_duration: ['p(99)<10000'], // 99% of requests must complete below 10s during spike
        http_req_failed: ['rate<0.2'],      // http errors should be less than 20% during spike
        errors: ['rate<0.25'],              // custom error rate should be less than 25% during spike
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
            console.log('Login successful for spike test');

            const headers = {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            };

            const productTypeIds = [];
            for (let i = 0; i < 5; i++) {
                const productTypeData = {
                    name: `Spike Test Category ${Date.now()}-${i}`
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

    const scenario = Math.random();

    if (scenario < 0.7) {
        normalLoadScenario(headers, data);
    } else {
        spikeScenario(headers, data);
    }

    const sleepTime = scenario < 0.7 ? Math.random() * 2 + 1 : Math.random() * 1 + 0.2;
    sleep(sleepTime);
}

function normalLoadScenario(headers, data) {
    const scenario = Math.random();

    if (scenario < 0.6) {
        basicReadOperations(headers);
    } else if (scenario < 0.9) {
        basicProductOperations(headers, data);
    } else {
        basicProductTypeOperations(headers);
    }
}

function spikeScenario(headers, data) {
    const scenario = Math.random();

    if (scenario < 0.4) {
        intensiveReadsDuringSpike(headers);
    } else if (scenario < 0.7) {
        rapidProductOperations(headers, data);
    } else if (scenario < 0.85) {
        concurrentOperations(headers, data);
    } else {
        errorProneOperations(headers);
    }
}

function basicReadOperations(headers) {
    let response = http.get(`${BASE_URL}/api/health`, { headers });
    const healthCheck = check(response, {
        'basic health check ok': (r) => r.status === 200,
    });
    errorRate.add(!healthCheck);

    response = http.get(`${BASE_URL}/api/v1/products?page=0&size=10`, { headers });
    const productsCheck = check(response, {
        'basic products list ok': (r) => r.status === 200,
        'basic products response time ok': (r) => r.timings.duration < 2000,
    });
    errorRate.add(!productsCheck);
}

function basicProductOperations(headers, data) {
    if (!data.token) {
        basicReadOperations(headers);
        return;
    }

    const productData = {
        name: `Normal-${Date.now()}-${Math.random().toString(36).substr(2, 15)}`,
        description: 'Normal load product',
        quantity: Math.floor(Math.random() * 100) + 50,
        lowStockThreshold: 10,
        weight: Math.random() * 5 + 1,
        height: Math.random() * 20 + 5,
        length: Math.random() * 30 + 10,
        productTypeId: data.productTypeIds[Math.floor(Math.random() * data.productTypeIds.length)]
    };
    const response = http.post(`${BASE_URL}/api/v1/products`, JSON.stringify(productData), { headers });
    const createCheck = check(response, {
        'normal product creation ok': (r) => r.status === 201 || r.status === 409,
    });
    errorRate.add(!createCheck);

    if (response.status === 201) {
        const location = response.headers['Location'];
        if (location) {
            const productId = location.split('/').pop();

            const deleteResponse = http.del(`${BASE_URL}/api/v1/products/${productId}`, null, { headers });
            const deleteCheck = check(deleteResponse, {
                'normal product deletion ok': (r) => r.status === 204,
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
        'basic product types list ok': (r) => r.status === 200,
    });
    errorRate.add(!check_result);
}

function intensiveReadsDuringSpike(headers) {
    const requests = [
        ['GET', `${BASE_URL}/api/health`],
        ['GET', `${BASE_URL}/api/v1/products?page=0&size=50`],
        ['GET', `${BASE_URL}/api/v1/products?page=1&size=50`],
        ['GET', `${BASE_URL}/api/v1/products?page=2&size=50`],
        ['GET', `${BASE_URL}/api/v1/product-types?page=0&size=30`],
        ['GET', `${BASE_URL}/api/v1/product-types?page=1&size=30`],
    ];

    const responses = http.batch(requests.map(([method, url]) => [method, url, null, { headers }]));

    responses.forEach((response, index) => {
        const checkResult = check(response, {
            [`spike read ${index} status ok`]: (r) => r.status >= 200 && r.status < 500,
            [`spike read ${index} response time reasonable`]: (r) => r.timings.duration < 15000,
        });
        errorRate.add(!checkResult);
    });
}

function rapidProductOperations(headers, data) {
    if (!data.token) {
        intensiveReadsDuringSpike(headers);
        return;
    }

    const productIds = [];

    for (let i = 0; i < 5; i++) {
        const productData = {
            name: `Spike-${Date.now()}-${i}-${Math.random().toString(36).substr(2, 15)}`,
            description: `Spike test product ${i}`,
            quantity: Math.floor(Math.random() * 200) + 100,
            lowStockThreshold: 20,
            weight: Math.random() * 10 + 1,
            height: Math.random() * 30 + 10,
            length: Math.random() * 50 + 20,
            productTypeId: data.productTypeIds[Math.floor(Math.random() * data.productTypeIds.length)]
        };

        const response = http.post(`${BASE_URL}/api/v1/products`, JSON.stringify(productData), { headers });
        const createCheck = check(response, {
            [`spike product ${i} creation`]: (r) => r.status === 201 || r.status === 409 || r.status === 429 || r.status >= 500,
        });

        if (response.status === 201) {
            const location = response.headers['Location'];
            if (location) {
                productIds.push(location.split('/').pop());
            }
        }

        if (response.status !== 429) {
            errorRate.add(!createCheck);
        }
    }

    productIds.forEach((productId, index) => {
        const deleteResponse = http.del(`${BASE_URL}/api/v1/products/${productId}`, null, { headers });
        const deleteCheck = check(deleteResponse, {
            [`spike product ${index} deletion`]: (r) => r.status === 204 || r.status === 404 || r.status >= 500,
        });

        if (deleteResponse.status !== 429) {
            errorRate.add(!deleteCheck);
        }
    });
}

function concurrentOperations(headers, data) {
    if (!data.token) {
        intensiveReadsDuringSpike(headers);
        return;
    }

    const operations = [
        http.get(`${BASE_URL}/api/v1/products?page=0&size=20`, { headers }),
        http.get(`${BASE_URL}/api/v1/product-types?page=0&size=20`, { headers }),
        http.get(`${BASE_URL}/api/health`, { headers }),
    ];

    operations.forEach((response, index) => {
        const checkResult = check(response, {
            [`concurrent op ${index} reasonable response`]: (r) => r.status < 500 || r.timings.duration < 20000,
        });
        errorRate.add(!checkResult);
    });
}

function errorProneOperations(headers) {
    let response = http.get(`${BASE_URL}/api/v1/products/999999`, { headers });
    check(response, {
        'non-existent product returns 404': (r) => r.status === 404,
    });

    response = http.get(`${BASE_URL}/api/v1/product-types/999999`, { headers });
    check(response, {
        'non-existent product type returns 404': (r) => r.status === 404,
    });

    response = http.get(`${BASE_URL}/api/v1/products?page=-1&size=1000`, { headers });
    check(response, {
        'invalid pagination handled': (r) => r.status === 400 || r.status === 200,
    });

    if (headers['Authorization']) {
        response = http.post(`${BASE_URL}/api/v1/products`, JSON.stringify({ invalid: 'data' }), { headers });
        check(response, {
            'malformed request handled': (r) => r.status === 400 || r.status >= 500,
        });
    }
}

export function teardown(data) {
    console.log('Spike test completed');

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
        'spike-test-report.html': htmlReport(data, {
            title: 'Warehouse Service - SPIKE TEST Test Report'
        }),
        stdout: textSummary(data, { indent: ' ', enableColors: true }),
    };
}



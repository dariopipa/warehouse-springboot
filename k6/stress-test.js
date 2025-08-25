import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

const errorRate = new Rate('errors');
export const options = {
    stages: [
        { duration: '2m', target: 50 }, 
        { duration: '5m', target: 50 },    
        { duration: '2m', target: 200 },   
        { duration: '5m', target: 200 },   
        { duration: '2m', target: 300 },   
        { duration: '5m', target: 300 },   
        { duration: '2m', target: 0 },     
    ],
    thresholds: {
        http_req_duration: ['p(95)<5000', 'p(99)<10000'], // Stress test: looser thresholds
        http_req_failed: ['rate<0.3'],      // Stress test: allow higher failure rate (30%)
        errors: ['rate<0.4'],               // Stress test: custom error rate up to 40%
    },
};

const BASE_URL = 'http://localhost:6565';

const testCredentials = {
    username: 'admin',
    password: 'admin123'
};

export function setup() {
    console.log('Starting stress test setup...');
    
    const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify(testCredentials), {
        headers: { 'Content-Type': 'application/json' },
    });
    
    if (loginRes.status !== 200) {
        console.error('Login failed:', loginRes.body);
        return { token: null, productTypeId: null };
    }
    
    let token;
    try {
        token = JSON.parse(loginRes.body).token;
        console.log('Login successful for stress test');
    } catch (e) {
        console.error('Could not parse login response:', loginRes.body);
        return { token: null, productTypeId: null };
    }
    
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
    
    const productType = {
        name: `StressTestType-${Date.now()}`,
        description: 'Product type for stress testing'
    };
    
    const productTypeRes = http.post(`${BASE_URL}/api/v1/product-types`, JSON.stringify(productType), { headers });
    let productTypeId = null;
    
    if (productTypeRes.status === 201) {
        try {
            productTypeId = JSON.parse(productTypeRes.body).id;
            console.log('Product type created for stress test:', productTypeId);
        } catch (e) {
            console.log('Product type created but could not parse response');
        }
    } else if (productTypeRes.status === 409) {
        const existingRes = http.get(`${BASE_URL}/api/v1/product-types?page=0&size=1`, { headers });
        if (existingRes.status === 200) {
            try {
                const data = JSON.parse(existingRes.body);
                if (data.content && data.content.length > 0) {
                    productTypeId = data.content[0].id;
                    console.log('Using existing product type for stress test:', productTypeId);
                }
            } catch (e) {
                console.log('Could not parse existing product types response');
            }
        }
    }
    
    return { token: token, productTypeId: productTypeId };
}

export default function (data) {
    if (!data.token) {
        console.log('No token available, running limited stress test');
        stressReadOperations();
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${data.token}`
    };

    const scenario = Math.random();
    
    if (scenario < 0.6) {
        stressReadOperations(headers);
    } else if (scenario < 0.9 && data.productTypeId) {
        stressProductOperations(headers, data.productTypeId);
    } else {
        stressHealthChecks(headers);
    }

    sleep(0.3);
}

function stressReadOperations(headers = {}) {
    const products = http.get(`${BASE_URL}/api/v1/products?page=0&size=10`, { headers });
    check(products, {
        'stress products list ok': (r) => r.status === 200,
        'stress products response time ok': (r) => r.timings.duration < 5000,
    }) || errorRate.add(1);

    const productTypes = http.get(`${BASE_URL}/api/v1/product-types?page=0&size=10`, { headers });
    check(productTypes, {
        'stress product types list ok': (r) => r.status === 200,
    }) || errorRate.add(1);
}

function stressProductOperations(headers, productTypeId) {
    const uniqueId = `${Math.random().toString(36).substring(2, 15)}-${Date.now()}`;
    
    const product = {
        name: `StressProduct-${uniqueId}`,
        description: 'Product for stress testing',
        price: 99.99,
        quantity: Math.floor(Math.random() * 100) + 1,
        productTypeId: productTypeId
    };

    const createRes = http.post(`${BASE_URL}/api/v1/products`, JSON.stringify(product), { headers });
    
    const createSuccess = check(createRes, {
        'stress product creation ok': (r) => r.status === 201 || r.status === 409,
    });
    
    if (!createSuccess) {
        errorRate.add(1);
        return;
    }

    if (createRes.status === 201) {
        const createdProduct = JSON.parse(createRes.body);
        const deleteRes = http.del(`${BASE_URL}/api/v1/products/${createdProduct.id}`, null, { headers });
        
        check(deleteRes, {
            'stress product deletion ok': (r) => r.status === 204 || r.status === 404,
        }) || errorRate.add(1);
    }
}

function stressHealthChecks(headers) {
    const health = http.get(`${BASE_URL}/api/health`, { headers });
    check(health, {
        'stress health check ok': (r) => r.status === 200,
    }) || errorRate.add(1);
}

export function handleSummary(data) {
    return {
        'stress-test-report.html': htmlReport(data, { 
            title: 'Warehouse Service - STRESS TEST Test Report' 
        }),
        stdout: textSummary(data, { indent: ' ', enableColors: true }),
    };
}

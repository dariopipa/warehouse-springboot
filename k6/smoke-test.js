import http from 'k6/http';
import { check, sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
  vus: 1,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<1500'],
    http_req_failed: ['rate<0.1']
  },
};

const BASE_URL = 'http://localhost:6565';

const credentials = {
  username: 'admin',
  password: 'admin123'
};

export function setup() {
  const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify(credentials), {
    headers: { 'Content-Type': 'application/json' },
  });

  if (loginRes.status === 200) {
    try {
      const token = JSON.parse(loginRes.body).token;
      console.log('Login successful for smoke test');
      return { token: token };
    } catch (e) {
      console.log('Login response is not valid JSON:', loginRes.body);
      return { token: null };
    }
  }

  console.log(`Login failed with status ${loginRes.status}:`, loginRes.body);
  return { token: null };
}

export default function (data) {
  const headers = { 'Content-Type': 'application/json' };
  if (data.token) {
    headers['Authorization'] = `Bearer ${data.token}`;
  }

  testGetProducts(headers);
  testGetProductTypes(headers);

  if (data.token) {
    testHealthCheck(headers);
    testAuthenticatedEndpoints(headers);
  }

  sleep(1);
}

function testHealthCheck(headers) {
  const response = http.get(`${BASE_URL}/api/health`, { headers });
  check(response, {
    'health check status is 200': (r) => r.status === 200,
    'health check response time < 500ms': (r) => r.timings.duration < 500,
  });
}

function testGetProducts(headers) {
  const response = http.get(`${BASE_URL}/api/v1/products?page=0&size=5`, { headers });
  check(response, {
    'products status is 200': (r) => r.status === 200,
    'products response time < 1s': (r) => r.timings.duration < 1000,
  });
}

function testGetProductTypes(headers) {
  const response = http.get(`${BASE_URL}/api/v1/product-types?page=0&size=5`, { headers });
  check(response, {
    'product types status is 200': (r) => r.status === 200,
  });
}

function testAuthenticatedEndpoints(headers) {
  const response = http.get(`${BASE_URL}/api/v1/audit-logs?page=0&size=5`, { headers });
  check(response, {
    'audit logs accessible or properly secured': (r) => r.status === 200 || r.status === 403,
  });
}

export function handleSummary(data) {
  return {
    'smoke-test-report.html': htmlReport(data, {
      title: 'Warehouse Service - Smoke Test Report'
    }),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}

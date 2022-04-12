import http from 'k6/http';
import { check } from 'k6';

const minSize = 20;

export const options = {
    stages: [
        { duration: '5s', target: 10 },
        { duration: '10s', target: 10 },
        { duration: '5s', target: 0 },
    ],
    teardownTimeout: '120s',
    thresholds: {
        http_req_failed: ['rate<0.0001'],
        http_req_duration: ['p(95)<200'],
        'checks{status:200}': ['rate>0.9999'],
    },
};

export function setup() {
    //create some initial pool of todo items
    const res = http.get(`http://${__ENV.HOSTNAME}/api/v1/todos`)
    const currentSize = JSON.parse(res.body).length;
    const data = [];
    for (let i = 0; i < minSize - currentSize; i++) {
        const body = JSON.stringify({
            deadline: 1649682543889,
            task: `test task ${i}`
        });
        const params = {
            headers: { 'Content-Type': 'application/json' },
        };
        const res = http.post(`http://${__ENV.HOSTNAME}/api/v1/todos/todo`, body, params);
        data.push(JSON.stringify(res.body).id);
    }
    return data;
}

export default function () {
    //create new todo
    const body = JSON.stringify({
        deadline: 1649682543889,
        task: `test task ${(Math.random() + 1).toString(36).substring(7)}`
    });
    const params = {
        headers: { 'Content-Type': 'application/json' },
    };
    const createRes = http.post(`http://${__ENV.HOSTNAME}/api/v1/todos/todo`, body, params);
    check(createRes, {
        'response status code is 200': (r) => r.status === 200,
    }, {status: '200'});

    const id = JSON.parse(createRes.body).id;

    //get created todo
    const oneRes = http.get(`http://${__ENV.HOSTNAME}/api/v1/todos/todo/${id}`)
    check(oneRes, {
        'response status code is 200': (r) => r.status === 200,
    }, {status: '200'});

    //list all todos
    const allRes = http.get(`http://${__ENV.HOSTNAME}/api/v1/todos`)
    check(allRes, {
        'response status code is 200': (r) => r.status === 200,
    }, {status: '200'});

    //delete todo
    const delRes = http.del(`http://${__ENV.HOSTNAME}/api/v1/todos/todo/${id}`)
    check(delRes, {
        'response status code is 200': (r) => r.status === 200,
    }, {status: '200'});
}

export function teardown(data) {
    data.forEach(id => {
        http.del(`http://${__ENV.HOSTNAME}/api/v1/todos/todo/${id}`);
    });
}
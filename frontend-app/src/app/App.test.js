import {rest} from 'msw'
import {setupServer} from 'msw/node'
import {render, fireEvent, waitFor, screen} from '@testing-library/react'
import '@testing-library/jest-dom'
import App from './App';

const todosUrl = 'http://localhost:8080/api/v1/todos';
const server = setupServer(
    rest.get(todosUrl, (req, res, ctx) => {
        return res(ctx.json([]));
    }),
);
HTMLCanvasElement.prototype.getContext = jest.fn();
window.alert = jest.fn();

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('renders todo form', async () => {
    const {container} = render(<App/>);
    await waitFor(() => expect(container.getElementsByClassName('empty-todo-list').length).toBe(1));
    expect(container.getElementsByClassName('todo-add-form').length).toBe(1);
});

test('refresh list when new todo added', async () => {
    const {container} = render(<App/>);
    let body;
    server.use(
        rest.post(`${todosUrl}/todo`, (req, res, ctx) => {
            body = req.body;
            body.id = 1;
            return res(ctx.json({}));
        }),
        rest.get(todosUrl, (req, res, ctx) => {
            return res(ctx.json([body]));
        }),
    );

    fireEvent.change(container.getElementsByClassName('todo-add-task')[0], {target: {value: 'test task'}})
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => screen.getByText('test task'));
});

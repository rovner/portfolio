import {rest} from 'msw'
import {setupServer} from 'msw/node'
import {render, fireEvent, waitFor, screen} from '@testing-library/react'
import '@testing-library/jest-dom'
import TodoList from './TodoList'

const todosUrl = 'http://localhost:8080/api/v1/todos';
const server = setupServer(
    rest.get(todosUrl, (req, res, ctx) => {
        return res(ctx.json([]));
    }),
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('renders empty todos', async () => {
    const { container } = render(<TodoList/>);
    expect(screen.getByText('Loading...')).toBeInTheDocument();
    await waitFor(() => screen.getByText('Nothing todo'));
});

test('renders 2 todos', async () => {
    server.use(
        rest.get(todosUrl, (req, res, ctx) => {
            return res(ctx.json([
                {id: 1, deadline: 1659682543889, task: "test 1"},
                {id: 2, deadline: 1659682543889, task: "test 2"}
            ]));
        }),
    )
    const { container } = render(<TodoList/>);
    await waitFor(() => screen.getByText('test 1'));
    await waitFor(() => screen.getByText('test 2'));
});

test('renders error', async () => {
    server.use(
        rest.get(todosUrl, (req, res, ctx) => {
            return res(ctx.status(500));
        }),
    )
    const { container } = render(<TodoList/>);
    await waitFor(() => screen.getByText('Internal Server Error'));
});

test('refresh todo list when todo is deleted', async () => {
    server.use(
        rest.get(todosUrl, (req, res, ctx) => {
            return res(ctx.json([
                {id: 1, deadline: 1659682543889, task: "test 1"}
            ]));
        }),
    )
    const { container } = render(<TodoList/>);
    await waitFor(() => screen.getByText('test 1'));
    let isDeletedOnServer = false;
    server.use(
        rest.get(todosUrl, (req, res, ctx) => {
            return res(ctx.json([]));
        }),
        rest.delete(`${todosUrl}/todo/1`, (req, res, ctx) => {
            isDeletedOnServer = true;
            return res();
        }),
    )
    fireEvent.click(screen.getByText('Delete'));
    await waitFor(() => screen.getByText('Nothing todo'));
    expect(isDeletedOnServer).toBe(true);
});
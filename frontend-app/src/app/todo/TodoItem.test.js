import {rest} from 'msw'
import {setupServer} from 'msw/node'
import {render, fireEvent, waitFor, screen} from '@testing-library/react'
import '@testing-library/jest-dom'
import TodoItem from './TodoItem';

const todoUrl = 'http://localhost:8080/api/v1/todos/todo';
const server = setupServer();
window.alert = jest.fn();

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('renders overdue todo', () => {
    const item = {
        id: 1,
        deadline: 1649682543889,
        task: "TEST TASK"
    }
    const {container} = render(<TodoItem item={item}/>);
    expect(container.getElementsByClassName('todo-item').length).toBe(1);
    expect(container.getElementsByClassName('overdue').length).toBe(1);
    expect(container.getElementsByClassName('todo-item-deadline').length).toBe(1);
    expect(screen.getByText('4/11/2022, 4:09:03 PM')).toBeInTheDocument();
    expect(screen.getByText('TEST TASK')).toBeInTheDocument();
});

test('renders not overdue todo', () => {
    const item = {
        id: 1,
        deadline: 1659682543889,
        task: "TEST TASK"
    }
    const {container} = render(<TodoItem item={item}/>);
    expect(container.getElementsByClassName('todo-item').length).toBe(1);
    expect(container.getElementsByClassName('overdue').length).toBe(0);
    expect(container.getElementsByClassName('todo-item-deadline').length).toBe(1);
});

test('delete todo', async () => {
    const mockHandle = jest.fn();
    let deleted = false;
    server.use(
        rest.delete(`${todoUrl}/1`, (req, res, ctx) => {
            deleted = true;
            return res(ctx.json({}));
        }),
    );
    const item = {
        id: 1,
        deadline: 1649682543889,
        task: "TEST TASK"
    }
    render(<TodoItem item={item} handleChange={mockHandle}/>);
    fireEvent.click(screen.getByText('Delete'));
    await waitFor(() => expect(deleted).toBe(true));
    expect(mockHandle.mock.calls.length).toBe(1);
});

test('error deleting todo', async () => {
    const mockHandle = jest.fn();
    server.use(
        rest.delete(`${todoUrl}/1`, (req, res, ctx) => {
            return res(ctx.status(500));
        }),
    );
    const item = {
        id: 1,
        deadline: 1649682543889,
        task: "TEST TASK"
    }
    render(<TodoItem item={item} handleChange={mockHandle}/>);
    fireEvent.click(screen.getByText('Delete'));
    await waitFor(() => expect(window.alert.mock.calls.length).toBe(1));
    expect(mockHandle.mock.calls.length).toBe(0);
});
import {rest} from 'msw'
import {setupServer} from 'msw/node'
import {render, fireEvent, waitFor, screen} from '@testing-library/react'
import '@testing-library/jest-dom'
import TodoForm from './TodoForm';

const todoUrl = 'http://localhost:8080/api/v1/todos/todo';
const server = setupServer();
HTMLCanvasElement.prototype.getContext = jest.fn();
window.alert = jest.fn();

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test('renders todo form', async () => {
    const {container} = render(<TodoForm handleChange={jest.fn}/>);
    expect(container.getElementsByClassName('todo-add-disclaimer').length).toBe(1);
    expect(container.getElementsByClassName('todo-add-deadline').length).toBe(1);
    expect(container.getElementsByClassName('todo-add-task').length).toBe(1);
    expect(container.getElementsByClassName('todo-add-submit').length).toBe(1);
});

test('submit new todo', async () => {
    let body;
    server.use(
        rest.post(todoUrl, (req, res, ctx) => {
            body = req.body;
            return res(ctx.json({}));
        }),
    );

    const {container} = render(<TodoForm handleChange={jest.fn}/>);

    fireEvent.change(container.getElementsByClassName('react-datetime-picker__inputGroup__day')[0], {target: {value: '01'}})
    fireEvent.change(container.getElementsByClassName('react-datetime-picker__inputGroup__month')[0], {target: {value: '01'}})
    fireEvent.change(container.getElementsByClassName('react-datetime-picker__inputGroup__year')[0], {target: {value: '2040'}})
    fireEvent.change(container.getElementsByClassName('react-datetime-picker__inputGroup__hour')[0], {target: {value: '12'}})
    fireEvent.change(container.getElementsByClassName('react-datetime-picker__inputGroup__minute')[0], {target: {value: '00'}})
    fireEvent.change(container.getElementsByClassName('todo-add-task')[0], {target: {value: 'test task'}})
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => expect(window.alert.mock.calls.length).toBe(1));
    expect(body.task).toBe('test task');
    expect(body.deadline).toBe(2209021200000);
});

test('submit new todo without task', async () => {
    render(<TodoForm handleChange={jest.fn}/>);
    fireEvent.click(screen.getByText('Submit'));
    expect(window.alert.mock.calls.length).toBe(1);
    expect(window.alert.mock.lastCall[0]).toBe('Please fill todo task!');
});

test('submit error', async () => {
    server.use(
        rest.post(todoUrl, (req, res, ctx) => {
            return res(ctx.status(500));
        }),
    );

    const {container} = render(<TodoForm handleChange={jest.fn}/>);

    fireEvent.change(container.getElementsByClassName('todo-add-task')[0], {target: {value: 'test task'}})
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => expect(window.alert.mock.calls.length).toBe(1));
    expect(window.alert.mock.lastCall[0]).toBe('Internal Server Error');
});

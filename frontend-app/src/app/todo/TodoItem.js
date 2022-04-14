import React from 'react';
import './TodoItem.css'

class TodoItem extends React.Component {

    constructor() {
        super();
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete(id) {
        fetch(`${process.env.REACT_APP_SERVER_URL}/api/v1/todos/todo/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
            })
            .then(() => this.props.handleChange())
            .catch(err => alert(err.toString()));
    }

    render() {
        const isOverdue = Date.now() > this.props.item.deadline;
        return <div className={"todo-item " + (isOverdue ? 'overdue' : '')}>
                <div className="todo-item-deadline">{new Date(this.props.item.deadline).toLocaleString()}</div>
                <div className="todo-item-task">{this.props.item.task}</div>
                <button className="todo-item-delete button" onClick={() => this.handleDelete(this.props.item.id)}>Delete</button>
            </div>;
    }
}

export default TodoItem
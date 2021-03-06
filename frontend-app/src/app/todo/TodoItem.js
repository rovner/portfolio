import React from 'react';
import './TodoItem.css'

class TodoItem extends React.Component {

    constructor() {
        super();
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete(id) {
        fetch(`${window._env_.BACKEND_API_URL}/api/v1/todos/todo/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                if (response.ok) {
                    this.props.handleChange();
                } else {
                    alert(response.statusText);
                }
            });
    }

    render() {
        const isOverdue = Date.now() > this.props.item.deadline;
        return <div className={"todo-item-wrapper " + (isOverdue ? 'overdue' : '')}>
            <div className="todo-item-deadline">{new Date(this.props.item.deadline).toLocaleString()}</div>
            <div className="todo-item-task">{this.props.item.task}</div>
            <button className="todo-item-delete button" onClick={() => this.handleDelete(this.props.item.id)}>Delete
            </button>
        </div>;
    }
}

export default TodoItem
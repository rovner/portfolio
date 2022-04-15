import React from "react";
import DateTimePicker from 'react-datetime-picker';
import './TodoForm.css'

class TodoItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            task: null,
            deadline: new Date(),
        };

        this.handleTaskChange = this.handleTaskChange.bind(this);
        this.handleDeadlineChange = this.handleDeadlineChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleTaskChange(event) {
        this.setState({task: event.target.value});
    }

    handleDeadlineChange(date) {
        this.setState({deadline: date});
    }

    handleSubmit(event) {
        event.preventDefault();
        if (this.state.task === null) {
            alert("Please fill todo task!");
            return;
        }
        fetch(`${process.env.REACT_APP_SERVER_URL}/api/v1/todos/todo`, {
             method: 'POST',
             body: JSON.stringify({
                 task: this.state.task,
                 deadline: this.state.deadline.getTime(),
             }),
             headers: {
                 'Content-Type': 'application/json'
             },
         })
            .then(response => {
                if (response.ok) {
                    alert('Todo was submitted');
                    this.props.handleChange();
                } else {
                    alert(response.statusText);
                }
            });
    }

    render() {
        return <div>
                <div className="todo-add-disclaimer">New Task:</div>
                <form className="todo-add-form" onSubmit={this.handleSubmit}>
                    <DateTimePicker className="todo-add-deadline" onChange={this.handleDeadlineChange} value={this.state.deadline} />
                    <textarea className="todo-add-task" value={this.state.value} onChange={this.handleTaskChange} />
                    <input className="button todo-add-submit" type="submit" value="Submit" />
                </form>
            </div>;
    }
}

export default TodoItem;
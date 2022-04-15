import React from 'react';
import TodoItem from './TodoItem'

class TodoList extends React.Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
        this.state = {
            items: null,
            error: null,
            loaded: false
        };
    }

    handleChange() {
        fetch(`${process.env.REACT_APP_SERVER_URL}/api/v1/todos`, {
               headers: {
                 'Content-Type': 'application/json'
               },
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error(response.statusText);
                }
            })
            .then(data => this.setState({
                items: data,
                loaded: true
            }))
            .catch(err => this.setState({
                error: err.message,
                loaded: true
            }));
    }

    componentDidMount() {
        this.handleChange();
    }

    render() {
        if (!this.state.loaded) {
            return <div className="loading-indicator">Loading...</div>;
        }
        if (this.state.error) {
            return <div className="error-message">{this.state.error}</div>;
        }
        if (this.state.items.length === 0) {
            return <div className="empty-todo-list">Nothing todo</div>
        }
        const items = this.state.items.map(item =>
            <TodoItem key={item.id} item={item} handleChange={this.handleChange}/>);
        return <div className="todo-list">{items}</div>;
    }
}

export default TodoList;

import React from 'react';
import './App.css';
import TodoList from './todo/TodoList'
import TodoForm from './todo/TodoForm'

class App extends React.Component {

    constructor() {
        super();
        this.handleChange = this.handleChange.bind(this);
        this.state = {
            key: Date.now()
        }
    }

    handleChange() {
        this.setState({
            key: Date.now()
        });
    }

    render() {
        return <div className="app">
            <TodoList key={this.state.key}/>
            <TodoForm handleChange={this.handleChange}/>
        </div>;
    }
}

export default App;

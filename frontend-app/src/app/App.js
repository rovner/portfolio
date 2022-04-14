import React from 'react';
import './App.css';
import TodoList from './todo/TodoList'
import TodoAdd from './todo/TodoAdd'

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
                <TodoAdd handleChange={this.handleChange}/>
            </div>;
    }
}

export default App;

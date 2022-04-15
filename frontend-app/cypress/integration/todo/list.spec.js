import api from '../../support/utils/api'
import homePage from '../../support/pages/home'

describe('Todo list', () => {
    it('Non empty list of todos', () => {
        cy.intercept(api.list.type, api.list.url, {fixture: 'todo/simple_todo_list.json'})
        homePage.open();
        homePage.todoList().toMatchImageSnapshot();
    })

    it('Empty list of todos', () => {
        cy.intercept(api.list.type, api.list.url, [])
        homePage.open();
        homePage.emptyList().toMatchImageSnapshot();
    })
})
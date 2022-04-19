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

    it('Error loading list of todos', () => {
        cy.intercept(api.list.type, api.list.url, {
            statusCode: 500,
            body: {
                "error": "Internal Server Error",
            },
        })
        homePage.open();
        homePage.errorMessage().toMatchImageSnapshot();
    })

    it('Hover todo', () => {
        // cy.viewport(1920, 1080);
        cy.intercept(api.list.type, api.list.url, {fixture: 'todo/single_todo_list.json'})
        homePage.open();
        homePage.hoverTodoByIndex(0);
        homePage.todoList().toMatchImageSnapshot({capture: 'fullPage'});
    })

    it('Delete todo', () => {
        cy.intercept(api.list.type, api.list.url, {fixture: 'todo/single_todo_list.json'})
        cy.intercept(api.delete.type, api.delete.url, {}).as('deleteReq');
        homePage.open();
        homePage.deleteTodoByIndex(0);
        cy.wait('@deleteReq');
    })
})
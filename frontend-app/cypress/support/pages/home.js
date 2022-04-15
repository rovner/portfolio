class HomePage {
    open() {
        cy.log("open home page");
        cy.visit('');
    }

    todoList() {
        return cy.get('.todo-list');
    }

    emptyList() {
        return cy.get('.empty-todo-list');
    }
}

export default new HomePage();
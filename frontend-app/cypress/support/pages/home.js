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

    errorMessage() {
        return cy.get('.error-message');
    }

    deleteTodoByIndex(index) {
        this.hoverTodoByIndex(index);
        cy.log("click on delete button");
        cy.get('.todo-item-delete').eq(index).click();
    }

    hoverTodoByIndex(index) {
        cy.log("hover on todo item #" + (index + 1));
        cy.get('.todo-item-wrapper').eq(index).realHover();
        cy.get('.todo-item-delete').should('be.visible');
    }
}

export default new HomePage();
class HomePage {
    open() {
        cy.log('open home page');
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

    form() {
        return cy.get('.todo-add-form');
    }
    deadlineInput() {
        return this.form().get('.todo-add-deadline');
    }

    deleteTodoByIndex(index) {
        this.hoverTodoByIndex(index);
        cy.log('click on delete button');
        cy.get('.todo-item-delete').eq(index).click();
    }

    hoverTodoByIndex(index) {
        cy.log(`hover on todo item #${index + 1}`);
        cy.get('.todo-item-wrapper').eq(index).realHover();
        cy.get('.todo-item-delete').should('be.visible');
    }

    clearDeadlineInput() {
        cy.log('clear calendar input');
        this.deadlineInput()
            .get('.react-datetime-picker__clear-button')
            .click()
            .blur();
    }

    setDeadline(day, month, year, hour, minute) {
        cy.log(`set deadline to ${day}/${month}/${year} ${hour};${minute}`);
        this.deadlineInput().get('.react-datetime-picker__inputGroup__day').type(day);
        this.deadlineInput().get('.react-datetime-picker__inputGroup__month').type(month);
        this.deadlineInput().get('.react-datetime-picker__inputGroup__year').type(year);
        this.deadlineInput().get('.react-datetime-picker__inputGroup__hour').type(hour);
        this.deadlineInput().get('.react-datetime-picker__inputGroup__minute').type(minute).blur();
    }

    typeTodoTsk(task) {
        cy.log(`type todo task '${task}'`);
        this.form().get('.todo-add-task').type(task);
    }

    submitTodo() {
        cy.log('submit todo');
        this.form().get('.todo-add-submit').click();
    }
}

export default new HomePage();
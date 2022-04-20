import api from '../../support/utils/api'
import homePage from '../../support/pages/home'

describe('Todo form', () => {
    beforeEach(() => {
        cy.intercept(api.list.type, api.list.url, []);
    });

    it('Select date in calendar', () => {
        homePage.open();
        homePage.clearDeadlineInput();
        homePage.form().toMatchImageSnapshot();
        homePage.setDeadline(31, 12, 2043, 13, 48);
        homePage.deadlineInput().toMatchImageSnapshot();
    })

    it('Submit todo', () => {
        cy.intercept(api.post.type, api.post.url, {}).as('submitReq');

        homePage.open();
        homePage.typeTodoTsk("task todo");
        homePage.submitTodo();

        cy.wait('@submitReq');
        cy.on('window:alert', (str) => {
            expect(str).to.equal('Todo was submitted');
        });
    })
})
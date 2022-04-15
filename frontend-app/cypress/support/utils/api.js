class Api {
    list = {
        url: `${Cypress.env('apiUrl')}/api/v1/todos`,
        type: 'GET'
    }
}

export default new Api();

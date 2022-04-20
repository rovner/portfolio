class Api {
    list = {
        url: `${Cypress.env('apiUrl')}/api/v1/todos`,
        type: 'GET'

    }
    delete = {
        url: `${Cypress.env('apiUrl')}/api/v1/todos/todo/*`,
        type: 'DELETE'
    }

    post = {
        url: `${Cypress.env('apiUrl')}/api/v1/todos/todo`,
        type: 'POST'
    }
}

export default new Api();

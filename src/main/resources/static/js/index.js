const baseUrl = 'http://localhost:8080'
const userUrl = baseUrl + '/api/user'
const adminUrl = baseUrl + '/api/admin'
const roleUrl = baseUrl + '/api/admin/role'

const alertPlaceholder = $('#liveAlertPlaceholder')
const loggedUserRolesEl = $('#logged-user-roles')
const navTabEl = $('#nav-tab')
const navNewUserEl = $('#nav-new-user')
const adminPageBtnEl = $('#admin-page-btn')
const userPageBtnEl = $('#user-page-btn')

class User {
    constructor(id = 0, name = '', password = '',
                surname = '', age = 0, email = '', roles = []) {
        this.id = id
        this.name = name
        this.password = password
        this.age = age
        this.email = email
        this.roles = roles
    }

    getFromForm(form) {
        const data = new FormData(form)

        this.id = data.get('id')
        this.name = data.get('name')
        this.age = data.get('age')
        this.email = data.get('email')
        this.password = data.get('password')

        const auth = new Roles()
        this.roles = Array.from(form.querySelector('#authorities').selectedOptions)
            .map(o => auth.addFromOption(o))
        return this
    }

    getFromObject(obj) {
        for (let attr in obj) {
            this[attr] = obj[attr]
        }
        return this
    }
}

class Role {
    constructor(id = 0, name = '') {
        this.id = id
        this.name = name
    }

    getFromOption(option) {
        this.id = option.value
        this.name = option.text
        return this
    }

    getFromObject(obj) {
        for (let attr in this) {
            this[attr] = obj[attr]
        }
        return this
    }

    toString() {
        return this.name.replace('ROLE_', '')
    }
}

class Roles extends Array {
    constructor() {
        super();
        this.roles = []
    }

    addFromObject(obj) {
        const role = new Role().getFromObject(obj)
        this.roles.push(role)
        return role
    }

    addFromOption(option) {
        const role = new Role().getFromOption(option)
        this.roles.push(role)
        return role
    }

    toString() {
        return this.roles.map(r => r.toString()).join(' ')
    }

    includes(searchElement) {
        return !!this.roles.filter(el => el.toString() === searchElement).length
    }
}

let allUsers = []
let userTableRows = []
let adminTableRows = []
let allAuthorities = {}

getLoggedUser()
    .then(user => {
        const auth = new Roles()
        user.roles.map(obj => auth.addFromObject(obj))
        loggedUserRolesEl.children()[0].innerHTML = user.email
        loggedUserRolesEl.children()[1].innerHTML = auth.toString()
        if (auth.includes('ADMIN')) {
            adminTableRows.push(getUserTableRowTemplate(user))
            adminPageBtnEl.addClass('active')
            getRoles()
                .then((roles) => {
                    const newUserAuthEl = $('#authorities')
                    newUserAuthEl.attr('size', roles.length)
                    newUserAuthEl.html(getAuthoritiesOptions(new User(), roles))
                    $('#newUserForm').submit(event => {
                        event.preventDefault()
                        createUser(new User().getFromForm(event.target))
                        $('#nav-users-table-tab')[0].click()
                        event.target.reset()
                    })
                })
            getUsers()
                .then((users) => {
                    renderUsersTable(users)
                    $('#users-tbody').click(event => {
                        event.preventDefault()
                        const delBtnIsPressed = event.target.id === 'userDeleteBtn'
                        const editBtnIsPressed = event.target.id === 'userEditBtn'
                        const userTableRowEl = event.target.parentElement.parentElement
                        let user = allUsers.filter(user => user.id == userTableRowEl.dataset.id)[0]
                        if (delBtnIsPressed) {
                            handleUserModifyButtons(user, 'delete', userTableRowEl)
                        }
                        if (editBtnIsPressed) {
                            handleUserModifyButtons(user, 'edit', userTableRowEl)
                        }
                    })
                })
            userPageBtnEl.click(() => {
                setTitles('User page', 'User information-page', 'About user')
                navTabEl.hide()
                adminPageBtnEl.removeClass('active')
                userPageBtnEl.addClass('active')
                $('#users-tbody').html(adminTableRows.join(''))
                $('#th-edit').hide()
                $('#th-delete').hide()
                $('#td-edit-btn').hide()
                $('#td-delete-btn').hide()
            })
            adminPageBtnEl.click(() => {
                setTitles('Admin panel', 'Admin panel', 'All users')
                navTabEl.show()
                userPageBtnEl.removeClass('active')
                adminPageBtnEl.addClass('active')
                $('#th-edit').show()
                $('#th-delete').show()
                renderUsersTable([])
            })
        } else {
            setTitles('User page', 'User information-page', 'About user')
            navTabEl.remove()
            navNewUserEl.remove()
            adminPageBtnEl.remove()
            userPageBtnEl.addClass('active')
            renderUsersTable([user])
            $('#th-edit').hide()
            $('#th-delete').hide()
            $('#td-edit-btn').hide()
            $('#td-delete-btn').hide()
        }
    })

function setTitles(tab, page, table) {
    $('#tab-title').text(tab)
    $('#page-title').text(page)
    $('#table-title').text(table)
}

function handleUserModifyButtons(user, type, userTableRowEl) {
    const userModal = createUserModalOnPage(user, type)
    userModal.click(event => {
        event.preventDefault()
        const closeBtnIsPressed = event.target.id === 'closeBtn'
            || event.target.id === 'crossBtn' || event.target.id === 'userModal'
        const modifyBtnIsPressed = event.target.id === 'modifyBtn'
        if (closeBtnIsPressed) {
            removeModalFromPage(userModal)
        }
        if (modifyBtnIsPressed) {
            const userIndex = allUsers.findIndex(aUser => aUser.id === user.id)
            if (type === 'delete') {
                deleteUser(user)
                    .then(() => deleteUserTableRow(userTableRowEl, userIndex))
            }
            if (type === 'edit') {
                const user = new User().getFromForm(document.getElementById('userForm'))
                editUser(user)
                    .then((user) => editUserTableRow(user, userIndex))
                    .catch(() => deleteUserTableRow(userTableRowEl, userIndex))
            }
            removeModalFromPage(userModal)
        }
    })
}

// Get logged user
async function getLoggedUser() {
    const user = await (await fetch(userUrl)).json()
    return user
}

// Get roles
async function getRoles() {
    const authorities = await (await fetch(roleUrl)).json()
    allAuthorities = authorities
    return authorities
}

// Get users
async function getUsers() {
    const users = await (await fetch(adminUrl)).json()
    allUsers = users
    return users
}

// Create user
async function createUser(user) {
    const response = await fetch(adminUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
    if (response.ok) {
        const user = await response.json()
        allUsers.push(user)
        renderUsersTable([user])
        alertMessage(`User ${user.email} is successfully created`, 'success')
    } else {
        alertMessage(`User ${user.email} is already exists`, 'danger')
    }
}

// Delete user
async function deleteUser(user) {
    const response = await fetch(`${adminUrl}/${user.id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    if (response.ok) {
        alertMessage(`User with id = ${user.id} is successfully deleted`, 'success')
    } else {
        alertMessage(`User with id = ${user.id} is not found`, 'danger')
    }
}

// Edit user
async function editUser(user) {
    const response = await fetch(`${adminUrl}/${user.id}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
    if (response.ok) {
        alertMessage(`User with id = ${user.id} is successfully edited`, 'success')
    } else {
        alertMessage(`User with id = ${user.id} is not found`, 'danger')
        return new Promise(function (resolve, reject) {
            reject()
        })
    }
    return response.json()
}

function alertMessage(message, type) {
    const wrapper = document.createElement('div')
    wrapper.innerHTML = [
        `<div class="alert alert-${type} alert-dismissible" role="alert">`,
        `   <div>${message}</div>`,
        '   <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
        '</div>'
    ].join('')
    alertPlaceholder.append(wrapper)
}

function createUserModalOnPage(user, type) {
    $('#modal-div').html(getModal(user, allAuthorities, type))
    const modal = $('#userModal')
    modal.modal('show')
    return modal
}

function removeModalFromPage(modal) {
    modal.modal('hide')
    modal.remove()
}

function deleteUserTableRow(trEl, index) {
    allUsers.splice(index, 1)
    userTableRows.splice(index, 1)
    trEl.remove()
}

function editUserTableRow(user, index) {
    allUsers[index] = user
    userTableRows[index] = getUserTableRowTemplate(user)
    $('#users-tbody').html(userTableRows.join(''))
}

function renderUsersTable(users) {
    users.forEach(user => {
        userTableRows.push(getUserTableRowTemplate(user))
    })
    $('#users-tbody').html(userTableRows.join(''))
}

function getUserTableRowTemplate(user) {
    return `
        <tr class="align-middle" data-id="${user.id}">
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>${user.roles.map(a => a.name.replace('ROLE_', '')).join(' ')}</td>
            <td id="td-edit-btn">
                <button type="button" class="btn text-white" id="userEditBtn"
                        style="background-color: #17a2b8">
                    Edit
                </button>
            </td>
            <td id="td-delete-btn">
                <button type="button" class="btn btn-danger" id="userDeleteBtn">
                    Delete
                </button>
            </td>
        </tr>
    `
}

function getModal(user, roles, type) {
    let hidden = ''
    let disabled = ''
    let btnClass = ''
    let btnText = ''
    if (type === 'edit') {
        btnClass = 'btn btn-primary'
        btnText = 'Save'
        user.password = ''
    } else if (type === 'delete') {
        hidden = 'hidden'
        disabled = 'disabled'
        btnClass = 'btn btn-danger'
        btnText = 'Delete'
    }
    return `
        <div class="modal" tabindex="-1" role="dialog" id="userModal">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${type[0].toUpperCase() + type.slice(1)} user</h5>
                            <button type="button" class="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close"
                                    id="crossBtn"></button>
                        </div>
                        <div class="modal-body">
                        
                            <form id="userForm" data-id="${user.id}">
                                <div class="row mb-4">
                                    <label for="id" class="fw-bold text-center">ID</label>
                                    <input type="text" id="id"
                                           class="form-control" readonly
                                           name="id" value="${user.id}">
                                </div>
    
                                <div class="row mb-4">
                                    <label for="name" class="fw-bold text-center">Name</label>
                                    <input type="text" id="firstName"
                                           class="form-control" 
                                           name="name"
                                           value="${user.name}"
                                           ${disabled}>
                                </div>
    
                                <div class="row mb-4">
                                    <label for="age" class="fw-bold text-center">Age</label>
                                    <input type="number" id="age"
                                           class="form-control" 
                                           name="age"
                                           value="${user.age}"
                                           ${disabled}>
                                </div>
    
                                <div class="row mb-4">
                                    <label for="email" class="fw-bold text-center">Email</label>
                                    <input type="email" id="email"
                                           class="form-control" 
                                           name="email"
                                           value="${user.email}"
                                           ${disabled}>
                                </div>
    
                                <div class="row mb-4" ${hidden}>
                                    <label for="password" class="fw-bold text-center">Password</label>
                                    <input type="password" id="password"
                                           class="form-control"
                                           name="password"
                                           value="${user.password}"
                                           ${disabled}>
                                </div>
    
                                <div class="row mb-4">
                                    <label for="authorities" class="fw-bold text-center">Role</label>
                                    <select id="authorities" 
                                            class="form-select"
                                            multiple name="authorities"
                                            ${disabled}
                                            size="${roles.length}">
                                        ${getAuthoritiesOptions(user, roles)}
                                    </select>
                                </div>
                            </form>
                            
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary"
                                    data-bs-dismiss="modal" id="closeBtn">Close
                            </button>
                            <button type="submit" class="${btnClass}" id="modifyBtn">
                                ${btnText}
                            </button>
                        </div>
                </div>
            </div>
        </div>
    `;
}

function getAuthorityName(a) {
    return a.name.replace('ROLE_', '')
}

function getAuthoritiesOptions(user, roles) {
    const userAuthoritiesId = user.roles.map(a => a.id)
    let res = ''
    roles.forEach(auth => {
        const roleId = auth.id
        const roleName = getAuthorityName(auth)
        if (userAuthoritiesId.includes(roleId)) {
            res += `<option value="${auth.id}" selected>${roleName}</option>`
        } else {
            res += `<option value="${auth.id}">${roleName}</option>`
        }
    })
    return res
}
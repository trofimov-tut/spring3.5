$(async function() {
    await thisUser();
});
async function thisUser() {
    fetch("http://localhost:8080/api/viewUser")
        .then(res => res.json())
        .then(data => {
            // Добавляем информацию в шапку
            $('#headerUsername').append(data.email);
            let roles = data.roles.map(role => " " + role.name);
            $('#headerRoles').append(roles);

            //Добавляем информацию в таблицу
            let user = `$(
            <tr>
                <td>${data.id}</td>
                <td>${data.name}</td>
                <td>${data.email}</td>
                <td>${roles}</td>)`;
            $('#userPanelBody').append(user);
        })
}



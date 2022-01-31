$(document).ready(function () {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/job4j_cinema/hall.do',
        dataType: 'json'
    }).done(function (data) {
        for (var ticket of data) {
            let row = ticket.row.toString();
            let cell = ticket.cell.toString();
            $('input[name="place"]').show().filter(function () {
                return $(this).attr('value').endsWith(row + cell);
            }).parents('td').replaceWith('<td>Занято</td>')
        }
    })
});
function Button() {
    let namePlace = $('input[name=place]:checked');
    if (namePlace.length < 1) {
        alert("Выберите места")
        return false;
    }
    let row = Math.floor(namePlace.val() / 10);
    let cell = namePlace.val() % 10;
    window.location.href = `http://localhost:8080/job4j_cinema/payment.html?row=${row}&cell=${cell}`;
}
let params = new URLSearchParams(window.location.search);
let row = params.get('row');
let cell = params.get('cell');
function confirm() {
    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/job4j_cinema/hall.do',
        data: {
            username: $('#username').val(),
            email: $('#email').val(),
            phone: $('#phone').val(),
            row: row,
            cell: cell
        },
        dataType: 'text'
    }).done(function () {
        return window.location.href = 'http://localhost:8080/job4j_cinema/index.html';
    }).fail(function (err) {
        console.log(err);
    })
}

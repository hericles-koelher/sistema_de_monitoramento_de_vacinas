async function getLotes(){
    $('#main').load('lotes.html', () => {
        $.get('http://localhost:8080/api/v1/lotes')
        .done(data => {
            console.log(data);
            preencheLotes(data);
        });
    });
}

function getLoteById(id){
    $('#main').load('lotes.html', () => {
        $.get(`http://localhost:8080/api/v1/lotes/${id}`)
        .done(data => {
            console.log(data);
            preencheLotes(data);
        });
    });
}

function preencheLotes(dados){
    if(!Array.isArray(dados)){
        dados = [dados];
    }  

    dados.forEach(element => {
        let table = document.getElementById('table_lotes');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let data = document.createElement('td');
        let dataF = (new Date(element.validade)).toLocaleDateString('pt-BR', {timeZone: 'UTC'});
        data.innerHTML = dataF;                
        tr.appendChild(data);

        let id_vacina = document.createElement('td');
        id_vacina.innerHTML = element.idVacina;
        tr.appendChild(id_vacina);

        table.appendChild(tr);
    });
}

function filtrar_lotes(){
    const id = document.getElementById('input_id_l').value;
    if(!id)
        alert("Forneça um ID válido!");
    else getLoteById(id);
}
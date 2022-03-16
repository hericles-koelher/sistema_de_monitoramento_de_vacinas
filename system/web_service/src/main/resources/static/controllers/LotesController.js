function preencheLotes(dados = ''){
    resultados = [
        {
            "id": 12,
            "validade" : "2022-04-01",
            "vacinaId" : 1
        },
        {
            "id": 13,
            "validade" : "2022-05-03",
            "vacinaId" : 2
        }
    ];

    //resultados = JSON.parse(resultados);

    resultados.forEach(element => {
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
        id_vacina.innerHTML = element.vacinaId;
        tr.appendChild(id_vacina);

        table.appendChild(tr);
    });
}

function filtrar_lotes(){
    const id = document.getElementById('input_id_l').value;
    if(!id)
        alert("Forneça um ID válido!");
    //req
    //.then() : 
    //preencheLeituras(); //passa os dados
}
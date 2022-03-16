function preencheVacinas(dados = ''){
    console.log('vacinas');
    let resultados = [
        {
            "id": 1,
            "nome": "Comirnaty (Pfizer/Wyeth)",
            "toleranciaEmMinutos" : 5,
            "temperaturaMinima" : 2,
            "temperaturaMaxima" : 8
        },
        {
            "id": 2,
            "nome": "Oxford/Covishield (Fiocruz/Astrazeneca)",
            "toleranciaEmMinutos": 10,
            "temperaturaMinima": 2,
            "temperaturaMaxima": 10
        }
    ];

    //resultados = JSON.parse(resultados);

    resultados.forEach(element => {
        let table = document.getElementById('table_vacinas');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let nome = document.createElement('td');
        nome.innerHTML = element.nome;
        tr.appendChild(nome);

        let tol = document.createElement('td')
        tol.innerHTML = element.toleranciaEmMinutos;
        tr.appendChild(tol);

        let tempMin = document.createElement('td');
        tempMin.innerHTML = element.temperaturaMinima;
        tr.appendChild(tempMin);

        let tempMax = document.createElement('td');
        tempMax.innerHTML = element.temperaturaMaxima;
        tr.appendChild(tempMax);

        table.appendChild(tr);
    });
}

function filtrar_vacinas(){
    const id = document.getElementById('input_id_v').value;
    if(!id)
        alert("Forneça um ID válido!");
    //req
    //.then() : 
    //preencheVacinas(); //passa os dados
}
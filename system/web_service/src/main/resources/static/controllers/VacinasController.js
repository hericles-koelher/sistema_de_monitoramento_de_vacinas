async function getVacinas(){
    $('#main').load('vacinas.html', () => {
        $.get('http://localhost:8080/api/v1/vacinas')
        .done(data => {
            console.log(data);
            preencheVacinas(data);
        });
    });
}

function getVacinaById(id){
    $('#main').load('vacinas.html', () => {
        $.get(`http://localhost:8080/api/v1/vacinas/${id}`)
        .done(data => {
            console.log(data);
            preencheVacinas(data);
        });
    });
}

function preencheVacinas(dados){
    if(!Array.isArray(dados)){
        dados = [dados];
    }  

    dados.forEach(element => {
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
    else getVacinaById(id);
}
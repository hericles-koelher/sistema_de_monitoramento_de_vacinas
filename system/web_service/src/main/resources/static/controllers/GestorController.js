function preencheGestores(dados = ''){
    let resultados = [
        {
            "id": 1,
            "nome": "Bruno",
            "email" : "hericles.koelher@ufes.com.br",
            "coordenadaX" : 45.3,
            "coordenadaY" : 32.4
        },
        {
            "id": 2,
            "nome": "Allan",
            "email" : "allan.f.souza@edu.ufes.br",
            "coordenadaX" : 65.3,
            "coordenadaY" : 23.7
        }
    ];

    //resultados = JSON.parse(resultados);

    resultados.forEach(element => {
        let table = document.getElementById('table_gestores');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let nome = document.createElement('td');
        nome.innerHTML = element.nome;
        tr.appendChild(nome);

        let email = document.createElement('td');
        email.innerHTML = element.email;
        tr.appendChild(email);

        let coordX = document.createElement('td');
        coordX.innerHTML = element.coordenadaX;
        tr.appendChild(coordX);

        let coordY = document.createElement('td');
        coordY.innerHTML = element.coordenadaY;
        tr.appendChild(coordY);

        table.appendChild(tr);
    });    
}

function filtrar_gestores(){
    const id = document.getElementById('input_id_g').value;
    if(!id)
        alert("Forneça um ID válido!");
    //req
    //.then() : 
    //preencheVacinas(); //passa os dados
}
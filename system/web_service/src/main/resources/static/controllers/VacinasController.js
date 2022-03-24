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

function postVacina(){
    Swal.fire({
        title: 'Cadastrar vacina:',
        html:
            '<label for="input1">Nome:</label><br>' +
            '<input id="input1" class=""><br>' +
            '<label for="input2">Tolerância:</label><br>' + 
            '<input type="number" id="input2" class=""><br>' +
            '<label for="input3">Temperatura máxima:</label><br>' + 
            '<input id="input3" type="number" class=""><br>' +
            '<label for="input4">Temperatura Mínima:</label><br>' + 
            '<input id="input4" type="number" class="">',
        showCancelButton: true,
        confirmButtonText: 'Cadastrar',
        showLoaderOnConfirm: true,
        preConfirm: function () {
            return new Promise(function (resolve) {
              resolve([
                $('#input1').val(),
                $('#input2').val(),
                $('#input3').val(),
                $('#input4').val()
              ])
            })
          },
        onOpen: function () {
            $('#input1').focus()
        },
        allowOutsideClick: () => !Swal.isLoading()
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: 'http://localhost:8080/api/v1/vacinas',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    nome : result.value[0],
                    toleranciaEmMinutos : result.value[1],
                    temperaturaMinima : result.value[2],
                    temperaturaMaxima : result.value[3]
                    })
            })
            .done(() => {
                Swal.fire("Cadastrado com sucesso!");
                getVacinas();
            });
        }
    })
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
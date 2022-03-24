async function getCamaras(){
    $('#main').load('camaras.html', () => {
        console.log('teste');
        $.get('http://localhost:8080/api/v1/camaras')
        .done(data => {
            console.log(data);
            preencheCamaras(data);
        });
    });
}

function getCamaraById(id){
    $('#main').load('camaras.html', () => {
        $.get(`http://localhost:8080/api/v1/camaras/${id}`)
        .done(data => {
            console.log(data);
            preencheCamaras(data);
        });
    });
}

function getCamaraByGestor(id){
    $('#main').load('camaras.html', () => {
        $.get(`http://localhost:8080/api/v1/camaras/gestor/${id}`)
        .done(data => {
            console.log(data);
            preencheCamaras(data);
        });
    });
}

function postCamara(){
    $.ajax({
        url: 'http://localhost:8080/api/v1/camaras',
        method: 'POST'
    })
    .done((response) => {
        Swal.fire("Câmara cadastrada com sucesso com o ID: " + response.id);
        console.log(response);
        getCamaras();
    });
}

function putLote(){
    Swal.fire({
        title: 'Atualizar Lote de câmara',
        html:
            '<label for="input1">ID câmara:</label><br>' +
            '<input id="input1" type="number"><br>' +
            '<label for="input2">ID lote:</label><br>' + 
            '<input type="number" id="input2">',            
        showCancelButton: true,
        confirmButtonText: 'Atualizar',
        showLoaderOnConfirm: true,
        preConfirm: function () {
            return new Promise(function (resolve) {
              resolve([
                $('#input1').val(),
                $('#input2').val(),
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
                url: `http://localhost:8080/api/v1/camaras/${result.value[0]}/atualizarLote`,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({
                    idLote: result.value[1]
                })
            })
            .done(() => {
                Swal.fire("Lote atualizado!");
                getCamaras();
            });    
        }
    })
       
}

function preencheCamaras(dados){
    if(!Array.isArray(dados)){
        dados = [dados];
    }        

    dados.forEach(element => {
        let table = document.getElementById('table_camaras');

        let tr = document.createElement('tr');
        let id = document.createElement('td');
        id.innerHTML = element.id;
        tr.appendChild(id);

        let temp = document.createElement('td');
        temp.innerHTML = element.temperaturaAtual;
        tr.appendChild(temp);

        let lote = document.createElement('td');
        lote.innerHTML = element.idLote;
        tr.appendChild(lote);        

        table.appendChild(tr);
    });
}

function selectCamaras(sel){
    let input = document.getElementById('input_id');
    let btn = document.getElementById('filter');
    if(sel.value == 0)
        input.disabled = btn.disabled = 'disabled';                                         
    else input.disabled = btn.disabled = '';
}

function filtrar_camaras(){
    const val = document.getElementById('sel_filter').value;
    const id = document.getElementById('input_id').value;
    if(!id)
        alert("Forneça um ID válido!");
    else if(val == 1)
        getCamaraById(id);
    else getCamaraByGestor(id);
}
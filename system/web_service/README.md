## Endpoints

---

Atualmente a API conta com 5 endpoints, 1 para cada um 
dos seguintes objetos:

- Camara
- Gestor
- Leitura
- Lote
- Vacina

### Endpoint _/camaras_

---

#### **[POST]** _/camaras_

**Descrição**: Registra uma câmara no sistema. Se o registro ocorrer
normalmente, então retorna uma resposta com código 201 e um JSON
com o recurso criado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/camaras
  ```
  
- Corpo da resposta:
  ```JSON
  {
    "id" : 0,
    "temperaturaAtual" : 16.4,
    "idLote" : 2
  }
  ```

#### **[GET]** _/camaras_

**Descrição**: Recupera todos os registros de camaras no sistema. 
Se a busca for bem sucedida, então retorna uma resposta com código 201 e um JSON
com uma lista do recurso desejado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/camaras
  ```
  
- Corpo da requisição:
  ```JSON
  {
    "temperaturaInicial" : 23.3,
    "idLote" : 0
  }
  ```

- Corpo da resposta:
  ```JSON
  [
    {
      "id" : 0,
      "temperaturaAtual" : 23.3,
      "idLote" : 0
    },
    {
      "id" : 1,
      "temperaturaAtual" : 17.8,
      "idLote" : 3
    }
  ]
    ```


#### **[GET]** _/camaras/{id}_

**Descrição**: Recupera uma câmara no sistema. Se a busca for
bem sucedida, então retorna uma resposta com código 201 e um JSON
com o recurso desejado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/camaras/0
  ```

- Corpo da resposta:
  ```JSON
    {
      "id" : 0,
      "temperaturaAtual" : 16.4,
      "idLote" : 2
    }
  ```

#### **[PUT]** _/camaras/{id}/atualizarLote_

**Descrição**: Atualiza o lote de uma determinada câmara no sistema.
Se a atualizção for bem sucedida, então retorna uma resposta com 
código 201 e um JSON com o recurso atualizado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/camaras/0/atualizarLote
  ```
  
- Corpo da requisição:
  ```JSON
  {
    "loteId" : 4
  }
  ```

- Corpo da resposta:
  ```JSON
  {
    "id" : 0,
    "temperaturaAtual" : 16.4,
    "idLote" : 4
  }
  ```

### Endpoint _/gestores_

---

#### **[POST]** _/gestores_

**Descrição**: Cria um gestor no sistema. Se o registro for
bem sucedido, então retorna uma resposta com código 201 e um JSON
com o recurso criado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/gestor
  ```
  
- Corpo da requisição:
  ```JSON
  {
    "nome" : "Relampago Marquinhos",
    "telefone" : "(27) 99825-4322"
  }
  ```

- Corpo da resposta:
  ```JSON
    {
      "id" : 0,
      "nome" : "Relampago Marquinhos",
      "telefone" : "(27) 99825-4322" 
    }
  ```

#### **[PUT]** _/gestores/{id}/atualizarLocalizacao_

**Descrição**: Atualiza a localização de um gestor no sistema. Se o registro for
atualizado, então retorna uma resposta com código 202 e um JSON
com o recurso atualizado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/gestor/0/atualizarLocalizacao
  ```

- Corpo da requisição:
  ```JSON
  {
    "coordenadaX" : 165.6,
    "coordenadaY" : 164.3
  }
  ```

- Corpo da resposta:
  ```
  A localização do gestor será atualizada!
  ```

### Endpoint _/leituras_

---

#### **[POST]** _/leituras_

**Descrição**: Envia a leitura registrada em uma camara. Se o registro for
feito corretamente, então retorna uma resposta com código 202 e um JSON
com o recurso atualizado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/leituras
  ```

- Corpo da requisição:
  ```JSON
  {
    "idCamara" : 0,
    "temperatura" : 23.5,
    "coordenadaX" : 165.6,
    "coordenadaY" : 164.3
  }
  ```

- Corpo da resposta:
  ```
  A leitura da câmara será registrada!
  ```

#### **[GET]** _/leituras_

**Descrição**: Recupera as leitura registradas. Se a operação for
bem sucedida, então retorna uma resposta com código 200 e um JSON
com o recurso desejado.

Exemplos:

- Endereço da requisição:
  ```HTTP
  http://localhost:8080/api/v1/leituras
  ```

- Corpo da resposta:
  ```JSON
  [
    {
    "id" : 0,
    "data" : ".........",
    "temperatura" : 23.5, 
    "coordenadaX" : 165.6,
    "coordenadaY" : 164.3,
    "idCamara" : 0
    },
    {
    "id" : 1,
    "data" : ".........",
    "temperatura" : 23.3, 
    "coordenadaX" : 165.6,
    "coordenadaY" : 164.3,
    "idCamara" : 0
    }
  ]
  ```

#### **[GET]** _/leituras/{id}_
#### **[GET]** _/leituras/camara/{id}_


### Endpoint _/lotes_

---

#### **[GET]** _/lotes_
#### **[GET]** _/lotes/{id}_


### Endpoint _/vacinas_

---

#### **[POST]** _/vacinas_
#### **[GET]** _/vacinas_
#### **[GET]** _/vacinas/{id}_



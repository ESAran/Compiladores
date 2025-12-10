<a id="readme-top"></a>
<!-- PROJECT LOGO -->
<br />
<div align="center">

  <h3 align="center">Compilador (GALS + Semântica + BIP)</h3>

  <p align="center">
    IDE e compilador acadêmico para linguagem com palavras‑chave em “latim”, gerando assembly BIP (.data/.text).
    <br />
    <br />
    <a href="Docs/GALS/">Gramática (GALS)</a>
    ·
    <a href="Docs/Testes de código/testes.txt">Testes prontos</a>
  </p>
</div>

<br>

<!-- SUMÁRIO -->
<details>
  <summary>Sumário</summary>
  <ol>
    <li>
      <a href="#sobre-o-projeto">Sobre o projeto</a>
      <ul>
        <li><a href="#construído-com">Construído com</a></li>
      </ul>
    </li>
    <li>
      <a href="#como-compilar">Como compilar</a>
    </li>
    <li>
      <a href="#como-executar-a-ide">Como executar a IDE</a>
    </li>
    <li>
      <a href="#exemplos-de-código">Exemplos de código</a>
      <ul>
        <li><a href="#função-com-parâmetros-e-retorno">Função com parâmetros e retorno</a></li>
        <li><a href="#chamada-de-função-em-outro-escopo">Chamada de função em outro escopo</a></li>
      </ul>
    </li>
    <li><a href="#estrutura-de-pastas">Estrutura de pastas</a></li>
    <li><a href="#licença">Licença</a></li>
    <li><a href="#contato">Contato</a></li>
  </ol>
</details>

<!-- SOBRE O PROJETO -->
## Sobre o projeto

Projeto de compilador usando **GALS** (léxico/sintático) e um **Semântico** em Java que gera **código BIP**.  
A IDE (Swing) permite colar o código‑fonte, compilar, visualizar o assembly gerado e inspecionar a tabela de símbolos.

> **Importante:** Para executar, rode a classe **`Compilador`**.  
> A gramática do GALS está em `docs/gramatica/` e os testes prontos em `docs/Testes de código/testes.txt`.

### Construído com

* Java (17+ recomendado)
* GALS (geração de analisadores)
* Swing (IDE)
* Backend de geração BIP (.data / .text)

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

<!-- EXEMPLOS -->
## Exemplos de código

### Função com parâmetros e retorno
```c
int soma(int a, int b) {
    reditus a + b;
}

vacuum principale() {
    int x, y, z;

    x = 2;
    y = 3;

    // uso do retorno diretamente em expressão
    z = soma(x, y) + 5;

    scribere(z);  // esperado: 10
}
```

### Chamada de função em outro escopo
```c
int inc(int v) {
    reditus v + 1;
}

vacuum principale() {
    int i, acc;

    i = 0;
    acc = 0;

    // chama a função dentro do laço (outro escopo)
    dum (i < 3) {
        acc = acc + inc(i);
        i = i + 1;
    }

    scribere(acc); // esperado: 6
}
```

> Mais exemplos em: **docs/Testes de código/testes.txt**

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

<!-- PASTAS -->
## Estrutura de pastas

```
.
├─ src/                          # Código-fonte Java (GALS + Semântico + IDE)
├─ docs/
│  ├─ gramatica/                 # Arquivos .lex/.sint do GALS
│  └─ Testes de código/
│     └─ testes.txt              # Casos prontos para colar na IDE
├─ out/                          # Classes compiladas (gerado pelo javac)
└─ README.md
```

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

<!-- LICENÇA -->
## Licença

Distribuído sob a licença do projeto. Veja `LICENSE` para mais detalhes.

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

<!-- CONTATO -->
## Contato

Eduardo Slomp Arán — duarans03@gmail.com

Links úteis:
- Gramática (GALS): `Docs/GALS/`
- Testes prontos: `Docs/Testes de código/testes.txt`

<p align="right">(<a href="#readme-top">voltar ao topo</a>)</p>

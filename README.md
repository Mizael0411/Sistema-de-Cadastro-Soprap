<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&height=120&color=e01a51&reversal=false"/>


# ⋆🐾° Sistema de Cadastro - SOPRAP

Sistema de gerenciamento desenvolvido para a **SOPRAP - Sociedade Protetora dos Animais de Apucarana**, com o objetivo de substituir o controle manual de informações da ONG por uma solução informatizada, prática e acessível.

Projeto acadêmico da disciplina de Práticas Extensionistas III, do curso de Bacharelado em Ciência da Computação - UNESPAR, Campus Apucarana.

## 🐾 Sobre a SOPRAP

Fundada em 1997, a SOPRAP atua na proteção e no bem-estar de animais em situação de rua em Apucarana. Realiza castrações diárias, resgates de animais em risco, atendimentos veterinários emergenciais e feiras de adoção. Hoje administra o Canil Municipal de Apucarana (Recanto São Francisco de Assis).

## 🐾 Problema

Anteriormente, todo o controle de cadastros, adoções, lares temporários e fluxo financeiro da instituição era realizado manualmente, o que gerava erros, duplicidade de dados, lentidão no acesso às informações e extrema dificuldade na prestação de contas.

## 🐾 Objetivo do sistema

Centralizar os dados da ONG em um ambiente seguro, intuitivo e com controle de acessos para otimizar o tempo dos voluntários e automatizar:
- **Animais:** Cadastros gerais, estado de gestação, controle vacinal e consultas veterinárias.
- **Lares Temporários:** Cadastro de tutores, capacidade máxima, controle automático de vagas e aceite de doenças transmissíveis.
- **Adoções:** Vinculação entre animal e adotante, registro de data, marcadores de adoções especiais e alteração automática de status.
- **Financeiro & Doações:** Registro de entradas (doações em dinheiro ou itens) e saídas (compras de produtos/insumos).
- **Relatórios:** Balanço financeiro (doações vs. gastos) e relatório de ocupação de lares com exportação para planilha eletrônica.
  
## 🐾 Requisitos e Arquitetura

### Requisitos Funcionais (RFs)
- **RF01** - Cadastro de animais (nome, idade, data de nascimento, sexo, gravidez, vacinação, última consulta)
- **RF02** - Cadastro de lares temporários (dados do tutor, capacidade, vagas, aceite de animais especiais)
- **RF03** - Registro de produtos/insumos comprados
- **RF04** - Controle de vacinas (exclusivamente Raiva e Virose)
- **RF05** - Gestão de adoções (vínculo animal-tutor, adoção especial, data)
- **RF06** - Registro de doações recebidas (dinheiro ou itens)
- **RF07** - Edição e exclusão de qualquer registro com janela de confirmação
- **RF08** - Controle de acesso e autenticação via login/senha
- **RF09** - Relatório financeiro (doações vs. gastos por período)
- **RF10** - Relatório de ocupação de lares e histórico de adoções
- **RF11** - Exportação de relatórios em formato de planilha eletrônica

### Requisitos Não Funcionais (RNFs) & Tecnologias
- **Linguagem:** Java (JDK 8+)
- **IDE Recomendada:** NetBeans IDE
- **Banco de Dados:** Relacional (SQL) via conexão JDBC
- **Arquitetura:** Aplicação Desktop (Swing / JavaFX)
- **Concorrência:** Suporte para uso simultâneo de no mínimo 3 usuários
- **Segurança:** Rotina de backup automático diário e integridade dos dados
- **Usabilidade:** Interface gráfica simples, intuitiva e de fácil manutenção

## 🐾 Equipe

- [Christiano Miquelão Bosso](https://github.com/christianombosso)
- [Luna Osti](https://github.com/Luna-Osti)
- [Maria Luiza Ayumi Saito](https://github.com/MariaLyoshiura)
- [Maria Rita Campana Peixoto](https://github.com/MariaCampanaP)
- [Mizael Augusto Soares Oliveira](https://github.com/Mizael0411)
- [Vallentina Rodrigues Costa Groetaers Sousa](https://github.com/Vallentinanina)

**Professor Orientador:** Dr. Lisandro Rogério Modesto.

## 🐾 Licença

Este projeto foi desenvolvido estritamente para fins acadêmicos e de extensão comunitária para a ONG SOPRAP.

<img width="100%" src="https://capsule-render.vercel.app/api?type=waving&height=120&color=e01a51&reversal=false&section=footer"/>

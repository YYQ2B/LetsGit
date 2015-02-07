LetsGit
=======

首先请你仔细阅读下列帮助。

LetsGit项目，是为了让大家学习和使用Git，任务一是一道坎，希望过去，你能得到帮助。

在项目中 **/forkstudy/** 中有学长学姐们一些推荐和联系信息。

*****
## 什么是版本管理？
每次你对文件做了修改，它就跟上一个版本不一样了。为了方便的记录文件内容的变化，以便将来查阅特定版本修订情况，我们需要使用版本管理系统来管理文件。

使用版本控制系统后，你可以明确的知道各个版本之间的变化，了解项目的进程，也方便在出现bug时找到原因。就算某天你胡来搞砸了整个项目，你也可以轻松将之恢复到原来的样子。目前已有的版本管理系统包括集中式版本管理系统和分布式管理系统等。


## 什么是Git？
Git是一种分布式版本控制系统。Git诞生于2005年，最初由Linus Torvalds编写，用作Linux内核代码的管理。Git是完全分布式的，设计简单，对非线性开发模式拥有强力支持，有能力高效管理类似Linux内核一样的超大规模项目，有着令人难以置信的非线性分支管理系统，可以应付各种复杂的项目开发需求。


## 什么是fork（派生）？
派生可理解为“复制”、“克隆”。通过派生操作，你可以完整复制git平台任一公开项目的代码到你的用户名名下。

##什么是Pull Requests（合并请求）？
当你派生某个项目，并在项目上做了一些修改后，如果你想把这些修改应用于“源项目”，您需要向源项目的作者提交“合并请求”，由他来把你的提交合并到源项目中。

## 如何提交合并请求
如何处理合并请求
什么是代码片？
代码片就是一段代码。它可能属于某个软件项目的一部分，也可能是一种算法，甚至只是一两句代码。 除了托管完整的软件项目，Git还可以进行代码片托管操作。


## 什么是wiki？
wiki是一个项目的知识管理系统，一般来说，用于存放项目的介绍、用户手册、功能说明等内容。

项目wiki除了支持在线编辑外，还可以作为一个git仓库来单独管理。您可以像管理git项目仓库一样使用git命令对wiki数据进行克隆和访问。


## 什么是markdown？
Markdown 是一种轻量级的标记语言，由John Gruber和Aaron Swartz创建，使其成为可读性最大并可再发行的可输入输出的格式。

项目的README文件和wiki都是Markdown格式的，文件后缀名为.md。

## 什么是issue？
除了直接参与项目的代码编写，你还可以通过为项目提意见和建议来参与项目。把你发现的bug、或者新的功能想法等通过Issue的方式提交给项目作者，也许下一个版本你的想法就被采纳了！


## 什么是社交编程？
社交编程是利用群体的智慧来进行合作编程的一种工作模式。这种模式采用派生/合并请求方式，让任何一个开发者都可以方便的向开源项目贡献代码。这也是一种优秀的代码评审机制，使得开发者之间的协作与交流变得顺畅而灵活，开发工作更加高效。

----

----
Final Mission
-------

> 向LetsGit主仓库提交

你能够在这里获取到Git软件：[点击跳转Git-scm主页][1]

在推送自己的提交前，请配置自己的global.config
配置用户名和邮箱

    $ git config --global user.name "yourname"
	$ git config --global user.email "youremail"

-------
有一群2B

[1]:http://www.git-scm.com/

------

# Git 基础语法

# GIT 101 -  Conceptos Basicos

## Init

Primero inicializar el repositorio con __git init <nombreRepositorio>__
tambien se puede inicializar una carpeta ya existente ej

    $ cd nombreRepositorio
    nombreRepositorio/$ git init

## Config

Para poder ver que configuraciones tenemos usamos __git config__
ej. el editor de commits o el nombre de usuario

    $ cd nombreRepositorio
    nombreRepositorio/$ git config -l

-l lista todas las configuraciones

    $ git config --global user.name "Su Nombre"
    $ git config --global user.email "Su Correo"
    $ git config --global core.editor "Su Editor ej. notepad"

Esto se hace para configurar la instalacion/repositorio

## Add

Para poder agregar algo a un commit hay que crearlo y luego agregarlo con __git add__

    $ vim README.md
    -- hacer algo alli --
    $ git add README.md

tambien se puede usar __git add -A__ para agregar a un commit todos los cambios
*USAR CON PRECAUCION*

## Commit

Hacer un commit (comprometerse con un cambio :P) luego de agregar los cambios a un commit
se hace usando __git commit__

    $ git commit

**-m** se puede usar para enviar en el commit el mensaje directamente ej. __git commit -m "Primer Commit"__
*USAR CON PRECAUCION*

## Status

Permite visualizar si hay archivos que tienen ambios que pendientes de hacer commit/sobreescribir

    $ git status

## Diff

El commando __git diff__ permite ver a detalle cuales son los cambios que estan
pendientes, con *--* muestra los archivos como se encuentran en el repositorio 
y con *++* los archivos como estan actualmente

## Checkout

Con el commando __git checkout <nombreArchivo/Ficher>__ reemplazo mis cambios
locales por los cambios que se encuentran en el repositorio, adicionalmente me
permite cambiar entre ramas (*ver adelante*)

## Log

__git log__ muestra el log de los commits que se han realizado, incluyendo el mensaje,
el autor, y la fecha

# rm / mv

Con __git rm__ o __git mv__ se pueden eliminar o mover archivos o ficheros que se encuentran
en el repositorio

# GIT 201 - Ramas y Mezclas

## branch

Una rama es un nuevo camino del codigo que debe ser manejado pero que no esta listo para
ser incluido en el *master* del repositorio, (la rama master es la rama por defecto en git) 

las ramas se usan por ejemplo para manejar nuevas funcionalidades

__git branch__ sin argumentos lista la ramas disponibles y con un asterisco la actual 
__git branch -v__ Lista las ramas y muestra el ultimo commit en cada una 
__git branch <nombreRama>__ crea una rama para trabajar
__git branch checkout <nombreRama>__ cambia la rama de trabajo a la rama seleccionada
__git branch checkout master__ vuelve a la rama principal (trunk?)
__git branch -D <nombreRama>__ elimina la rama

Todos los commits que se realizen estando trabajando en una rama quedan en la rama y se puede
cambiar entre ramas sin que los cambios realizados en una u otra se vean reflejados antes 
de hacer una mezcla (*ver adelante*)

## merge

Luego de tener los cambios en una rama y estar seguros de estos hay que realizar una mezcla
existen diferentes estrategias para mezclar ej.
    
    branch->master
    master->branch
    branch->branch
    (branch<->branch)->branch

El commando __git merge__ nos permite hacer todo este tipo de mezclas, sin embargo solo vamos a
ver la mas basica, luego de realizar los cambios en nuestra rama:

    $ git checkout master                # pasamos al master
    $ git merge <nombreRama>             # mezclamos la rama en nuestro master

Si existen conflictos git nos mostrara los archivos donde se presentaron para que sean resueltos
manualmente

## tag / show

Cuando estamos seguros de una version de nuestro codigo y queremos marcarla como segura / estable
podemos crear un tag con __git tag <nombre del tag>__ para que quede una copia del repositorio
en ese momento, para listar los tags lo hacemos con __git tag -l__ y para ver especificamente que 
tiene un tag lo hacemos con __git show <nombre del tag>__

# GIT 301 - Compartiendo el codigo

## .gitignore

Algunos archivos no queremos que sean controlados o guardar copias de estos en el repositorio
por ejemplo .class / .pyc / .py~ etc, para esto creamos un archivo .gitignore que contiene
mediante expresiones regulares los archivos y tipos de archivos que no queremos controlar

## clone

Un repositorio de git esta listo para ser compartido desde el primero momento, para hacer una
copia de un repositorio se debe usar __git clone <ubicacion del repo>__

## SSH Keys

Antes de poder compartir nuestro codigo o colaborar con otros repositorios debemos identificarnos
de una forma que garantize nuestra autenticidad, para esto se usa la generacion de llaves SSH

    $ ssh-keygen

Esto nos genera una llave publica y una llave privada, debemos entregarle la llave publica a la 
persona que queremos que confie en nuestro codigo (ej. github)

## remote

Para poder conectarnos con un repositorio fuera del que tenemos tenemos tenemos que agregar un 
repositorio remoto con el commando __git remote add <nombre remoto> <ubicacion del repo>__ se
puede usar __git remote rm <nombre remoto>__ para borrarlo

## push

Luego de estar conectados a un repositorio podemos _empujar_ al repositorio remoto los cambios
que tenemos en nuestro repositorio local mediante __git push <nombre remoto> <nombre rama>__
por defecto git intenta empujar al remoto origin y a la rama master

## pull y fetch

Al igual que podemos empujar nuestros cambios a un repositorio remoto podemos jalar cambios de
un repositorio remoto a nuestro repositorio local bien sea a nuestro carpeta de trabajo (pull) 
o a la copia de nuestro repositorio (fetch) usando los comandos

__git pull <nombre remoto> <nombre rama>__
__git fetch <nombre remoto> <nombre rama>__

## Fork?

Un fork no es un concepto de Git, es mas una conveniencia social, donde se hace el clone de un 
repositorio bajo un nombre propio, como git genera una copia del repositorio a traves de un
clone, podemos trabajarlo como un

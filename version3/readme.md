# Tercera version:
Version en la cual los humanos se mueven un grid. <br>
Se define un __GridValueLayer__ para _numero de mosquitos_ y para _probabilidad de infeccion_ (ambos valores genéricos y relacionados el uno con el otro). <br>
Se crean mosquitos en cada patch uniformemente aleatorio en el rango [0,150]. <br>
La probabilidad de contagio (infeccion) se calcula de la siguiente manera: <br>
_contagio = 0.2 + mosqs/250_

## Para poder correr el modelo:
En la carpeta 2302_2 se encuentras dos archivos .java y un context.xml. Agregar los archivos como normalmente se ha hecho. Cuando se vaya a correr el modelo: 
- __Data Loaders__: como siempre
- __Displays__: crear dos displays (uno para cada __GridValueLayer__) porque no se puede hacer un display de los dos grids al mismo tiempo. 

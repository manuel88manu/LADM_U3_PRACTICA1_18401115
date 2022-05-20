package mx.edu.ladm_u3_practica1_18401115.ui.slideshow

import android.R
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mx.edu.ladm_u3_practica1_18401115.DataBase
import mx.edu.ladm_u3_practica1_18401115.Mascota
import mx.edu.ladm_u3_practica1_18401115.MascotaActualizar
import mx.edu.ladm_u3_practica1_18401115.Propietario
import mx.edu.ladm_u3_practica1_18401115.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var IDs = ArrayList<String>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mostrarPropietario("")
        mostrarMascotaBuscada("","CURP")

        binding.insertarMascota.setOnClickListener {
            var id = binding.txtcurp.text.toString()
            if (id == "") {
                AlertDialog.Builder(requireContext())
                    .setMessage("Debe poner una CURP del propietario")
                    .setNeutralButton("ACEPTAR") {d,i -> }
                    .show()
                return@setOnClickListener
            } else {

                if(!Propietario(requireContext()).mostrarUnaPersona(binding.txtcurp.text.toString())){
                    AlertDialog.Builder(requireContext())
                        .setMessage("EL CURP INGRESADO NO EXISTE EN LA BD")
                        .setNeutralButton("ACEPTAR") {d,i -> }
                        .show()
                    return@setOnClickListener

                }

                if (binding.txtnombreMascota.text.toString() == "") {
                    AlertDialog.Builder(requireContext())
                        .setMessage("Olvido agregar nombre de la mascota")
                        .setNeutralButton("ACEPTAR") {d,i -> }
                        .show()
                    return@setOnClickListener
                } else {
                    var mascota = Mascota(requireContext())

                    mascota.nombre = binding.txtnombreMascota.text.toString()
                    mascota.raza = binding.txtRaza.text.toString()
                    mascota.curp = binding.txtcurp.text.toString()

                    var resultado = mascota.insertar()
                    if(resultado) {
                        Toast.makeText(requireContext(),"La Mascota fue registrada con exito", Toast.LENGTH_LONG).show()
                        mostrarMascotaBuscada("","CURP")

                        binding.txtRaza.setText("")
                        binding.txtcurp.setText("")
                        binding.txtnombreMascota.setText("")
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setTitle("ERROR")
                            .setMessage("Error: Accion no ejecutada")
                            .show()
                    }
                }
            }
        }

        binding.radBuscarNombre.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarMascotaBuscada(cadenaBuscar,"NOMBRE_MASCOTA")}
        }

        binding.radTodos.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                mostrarMascotaBuscada("","CURP")
               }
        }



        binding.radBuscarRaza.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarMascotaBuscada(cadenaBuscar,"RAZA")}
        }

        binding.radBuscarCurp.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarMascotaBuscada(cadenaBuscar,"CURP")}
        }



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun mostrarPropietario(busqueda:String) {
        var pro = Propietario(requireContext()).buscarPropietario(busqueda)
        var listaDatos = ArrayList<String>()
        var info = Propietario(requireContext())

        IDs.clear()

        (0..pro.size-1).forEach {
            val dueno = pro.get(it)
            info.curp = dueno.curp
            info.nombre = dueno.nombre

            listaDatos.add(dueno.curp + " | "+ info.getName())
            IDs.add(dueno.curp)
        }
        binding.listaPropietario.adapter = ArrayAdapter(requireContext(),
            R.layout.simple_list_item_1,listaDatos)

    }

    fun mostrarMascotaBuscada(busqueda:String, buscar:String) {
        var masc = Mascota(requireContext()).buscarDatoMascota(busqueda,buscar)
        var listaDatos = ArrayList<String>()
        var Data = Mascota(requireContext())

        IDs.clear()
        (0..masc.size-1).forEach {
            val cotas = masc.get(it)
            Data.curp = cotas.id_mascota
            Data.nombre = cotas.nombre
            Data.raza = cotas.raza
            Data.curp = cotas.curp

            listaDatos.add(Data.datosMascota())
            IDs.add(cotas.id_mascota)
        }
        binding.listaMascotas.adapter = ArrayAdapter(requireContext(),
            R.layout.simple_list_item_1,listaDatos)
        binding.listaMascotas.setOnItemClickListener { adapterView, view, indice, l ->
            val idLista = IDs.get(indice)
            val pet = Mascota(requireContext()).mostrarMascota(idLista)

            AlertDialog.Builder(requireContext())
                .setMessage("¿Desea Elimnar o Modificar a  ${pet.nombre} ?")
                .setNegativeButton("Eliminar") {d,i ->
                    pet.eliminar()
                    mostrarMascotaBuscada("","CURP")
                }
                .setPositiveButton("Actualizar") {d,i ->
                    val otraVentana = Intent(requireActivity(), MascotaActualizar::class.java)
                    otraVentana.putExtra("mascotaActualizar", pet.id_mascota)
                    startActivity(otraVentana)
                }
                .setNeutralButton("Cerrar") {d,i -> }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        mostrarMascotaBuscada("","CURP")
    }
}
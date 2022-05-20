package mx.edu.ladm_u3_practica1_18401115.ui.gallery


import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import mx.edu.ladm_u3_practica1_18401115.Mascota
import mx.edu.ladm_u3_practica1_18401115.Propietario
import mx.edu.ladm_u3_practica1_18401115.PropietarioActualizar
import mx.edu.ladm_u3_practica1_18401115.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    var IDs = ArrayList<String>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mostrarPropietariosBuscados("","CURP")

        binding.insertar.setOnClickListener {
            var propietario = Propietario(requireContext())

            propietario.telefono = binding.etTelefono.text.toString()
            propietario.edad = binding.etEdad.text.toString().toInt()
            propietario.curp = binding.etCurp.text.toString()
            propietario.nombre = binding.etNombre.text.toString()


            var resultado = propietario.insertar()
            if(resultado) {
                Toast.makeText(requireContext(),"NUEVO PROPIETARIO  SE INGRESO", Toast.LENGTH_LONG).show()
                mostrarPropietariosBuscados("","CURP")
                limpiar()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("ERROR AL INSERTAR")
                    .setMessage("ERROR AL INSERTAR, INTENTE NUEVAMENTE")
                    .show()
            }
        }
        binding.radBuscar.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarPropietariosBuscados(cadenaBuscar,"CURP")
            }
        }

        binding.radBuscarNombre.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarPropietariosBuscados(cadenaBuscar,"NOMBRE")}
        }

        binding.radBuscarEdad.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarPropietariosBuscados(cadenaBuscar,"EDAD")}
        }

        binding.radBuscarTelefono.setOnClickListener {
            if(binding.etBuscar.text.toString() == ""){
                Toast.makeText(requireContext(),"Ingrese un Dato para Buscar", Toast.LENGTH_LONG)
                    .show()
            }else{
                var cadenaBuscar = binding.etBuscar.text.toString()
                mostrarPropietariosBuscados(cadenaBuscar,"TELEFONO")}
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        mostrarPropietariosBuscados("","CURP")
    }



    fun limpiar() {
        binding.etCurp.setText("")
        binding.etEdad.setText("")
        binding.etTelefono.setText("")
        binding.etNombre.setText("")
    }

    fun mostrarPropietariosBuscados(cadenaBuscar:String, filtro:String) {
        var propie = Propietario(requireContext()).mostrarPorDato(cadenaBuscar,filtro)
        var infoPro= ArrayList<String>()
        var datosProp = Propietario(requireContext())
        IDs.clear()

        (0..propie.size-1).forEach {
            val persona = propie.get(it)
            datosProp.curp = persona.curp
            datosProp.nombre = persona.nombre
            datosProp.telefono = persona.telefono
            datosProp.edad = persona.edad

            infoPro.add(datosProp.contenido())
            IDs.add(persona.curp)
        }
        binding.listaPropietario.adapter = ArrayAdapter(requireContext(),R.layout.simple_list_item_1,infoPro)
        binding.listaPropietario.setOnItemClickListener { adapterView, view, indice, l ->
            val curpLista = IDs.get(indice)
            val propietario = Propietario(requireContext()).mostrarPropietario(curpLista)
            val mascotaEliminada=Mascota(requireContext())

            AlertDialog.Builder(requireContext())
                .setTitle("ATENCIÓN")
                .setMessage("¿Desea Eliminar o Actualizar a ${propietario.nombre} ?")
                .setNegativeButton("Eliminar") {d,i ->
                    Toast.makeText(requireContext(),"Eliminacion Exitosa!!", Toast.LENGTH_LONG).show()
                    propietario.eliminar(curpLista)
                    mascotaEliminada.eliminarPorDueño(curpLista)
                    mostrarPropietariosBuscados("","CURP") //mostrar todos
                }
                .setPositiveButton("Actualizar") {d,i ->
                    val intentVentanaActualizar = Intent(requireActivity(), PropietarioActualizar::class.java)
                    intentVentanaActualizar.putExtra("propietarioActualizar", propietario.curp)
                    startActivity(intentVentanaActualizar)
                }
                .setNeutralButton("Cerrar") {d,i -> }
                .show()

        }
    }

}
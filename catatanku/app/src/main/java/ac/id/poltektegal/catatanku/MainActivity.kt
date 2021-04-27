import ac.id.poltektegal.catatanku.Config.Notes
import ac.id.poltektegal.catatanku.NoteAdapter
import ac.id.poltektegal.catatanku.R
import ac.id.poltektegal.catatanku.config.DatabaseNotes
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.Observable
import io.reactivex.rxjava3.scheduler.Schedulers
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var notesDatabase: DatabaseNotes? = null
    private var dialogAddNote: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesDatabase = DatabaseNotes.getInstance(this)

        showNote()

        fab.setOnClickListener {
            showDialogAddNote()
        }

    }

    private fun saveNote(notes: Notes){
        Observable.fromCallable { notesDatabase?.notesDao()?.insert(notes) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, "Note berhasil disimpan", Toast.LENGTH_SHORT).show()

                //reload data
                showNote()

                dialogAddNote?.dismiss()

            }, {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                dialogAddNote?.dismiss()
            })
    }

    private fun updateNote(notes: Notes){
        Observable.fromCallable { notesDatabase?.notesDao()?.update(notes) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, "Note berhasil diupdate", Toast.LENGTH_SHORT).show()

                //reload data
                showNote()

                dialogAddNote?.dismiss()

            }, {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                dialogAddNote?.dismiss()
            })
    }

    private fun deleteNote(notes: Notes?){
        Observable.fromCallable { notesDatabase?.notesDao()?.delete(notes!!) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, "Note berhasil didelete", Toast.LENGTH_SHORT).show()

                //reload data
                showNote()

                dialogAddNote?.dismiss()

            }, {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                dialogAddNote?.dismiss()
            })
    }

    private fun showNote(){
        Observable.fromCallable { notesDatabase?.notesDao()?.getAll() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                listNote.adapter = NoteAdapter(it, object : NoteAdapter.OnClickListener {
                    override fun update(item: Notes?) {
                        showDialogUpdateNote(item)
                    }

                    override fun delete(item: Notes?) {
                        AlertDialog.Builder(this@MainActivity).apply {
                            setTitle("Delete")
                            setMessage("Anda yakin delete note ini ?")
                            setCancelable(false)
                            setPositiveButton("Ya") { dialog, i ->
                                deleteNote(item)
                                dialog.dismiss()
                            }
                            setNegativeButton("Tidak") { dialog, i ->
                                dialog.dismiss()
                            }
                        }.show()
                    }

                })

            }, {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            })
    }

    private fun showDialogAddNote() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formatedDate = formatter.format(date)

        dialogView.btnSave.setOnClickListener {
            if (dialogView.etNote.text.toString().isNotEmpty()){
                saveNote(Notes(null, dialogView.etNote.text.toString(), formatedDate))
            } else {
                dialogView.etNote.error = "Note harus diisi"
            }
        }

        dialogView.close.setOnClickListener {
            dialogAddNote?.dismiss()
        }

        dialogAddNote = dialog.create()
        dialogAddNote?.setCanceledOnTouchOutside(true)
        dialogAddNote?.show()

    }

    private fun showDialogUpdateNote(item: Notes?) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formatedDate = formatter.format(date)

        dialogView.btnSave.text = "Update"
        dialogView.etNote.setText(item?.note ?: "")
        dialogView.btnSave.setOnClickListener {
            if (dialogView.etNote.text.toString().isNotEmpty()){
                updateNote(Notes(item?.id, dialogView.etNote.text.toString(), formatedDate))
            } else {
                dialogView.etNote.error = "Note harus diisi"
            }
        }

        dialogView.close.setOnClickListener {
            dialogAddNote?.dismiss()
        }

        dialogAddNote = dialog.create()
        dialogAddNote?.setCanceledOnTouchOutside(true)
        dialogAddNote?.show()

    }


}
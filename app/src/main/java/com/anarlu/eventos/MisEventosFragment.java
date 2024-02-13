package com.anarlu.eventos;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;


public class MisEventosFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventoAdapter adapter;
    private List<Evento> eventos;

    private Button logout,borrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ImageView user2,creacion;
    private Toolbar toolbar;
    private static final int REQUEST_CAMERA_PERMISSION=2020;

    private UCrop.Options options;

    private GoogleSignInClient mGoogleSignInClient;
    private Drawable persona;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mis_eventos, container, false);

        // Configuración de la Toolbar
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventos=new ArrayList<>();
        adapter=new EventoAdapter(eventos);
        recyclerView.setAdapter(adapter);

        mFirestore=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        logout = view.findViewById(R.id.logout);
        user2 = view.findViewById(R.id.imageView2);
        borrar=view.findViewById(R.id.delete);
        options = new UCrop.Options();
        creacion=view.findViewById(R.id.imageView5);

        FirebaseApp.initializeApp(/*context=*/ getActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        user2.setScaleType(ImageView.ScaleType.FIT_XY);

        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90656351526-1hp02rmkk4ip4fnfbboj3b441ml7e1f1.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);

        int idImagenPredeterminada = getResources().getIdentifier("person", "drawable", getActivity().getPackageName());

        if (idImagenPredeterminada != 0) {
            persona = getResources().getDrawable(idImagenPredeterminada);
        } else {
            Toast.makeText(getActivity(), "Imagen no dispobible", Toast.LENGTH_SHORT).show();
        }

        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_CAMERA_PERMISSION);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logout();

            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarUsuario();
            }
        });

        creacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearNuevoEvento();
            }
        });

        if(mAuth.getCurrentUser()!=null){
            String userId=mAuth.getCurrentUser().getUid();
            mFirestore.collection("Eventos").whereEqualTo("ID_usuario",userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        eventos.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Evento evento = document.toObject(Evento.class);
                            eventos.add(evento);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configuración de la Toolbar
        toolbar.setTitle("Mis Eventos");

        creacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrearNuevoEvento();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser Fuser = mAuth.getCurrentUser();
        if (Fuser == null) {
            irLogin();
        } else {
            if(Fuser.getPhotoUrl()!=null){
                loadFirebaseImage(Fuser.getPhotoUrl());
            }else {
                Uri imagenUri=obtenerNuevaImagenSeleccionada();
                if(imagenUri!=null){
                    cargarNuevaImagen(imagenUri);
                }else {
                    if(Fuser.getPhotoUrl()!=null){
                        loadFirebaseImage(Fuser.getPhotoUrl());
                    }
                }
            }

            // Aquí es donde añades el código para descargar la imagen de Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("perfil/"+Fuser.getUid());

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File finalLocalFile = localFile;
            pathReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // El archivo se ha descargado con éxito
                            Uri downloadedImageUri = Uri.fromFile(finalLocalFile);
                            cargarNuevaImagen(downloadedImageUri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // El archivo no se ha descargado correctamente
                        }
                    });
        }
    }

    // Agregar los métodos onCreateOptionsMenu y onOptionsItemSelected para manejar el menú de la Toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Acción para el elemento de menú de ajustes
            Toast.makeText(requireActivity(), "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            // Acción para el elemento de menú de perfil
            Toast.makeText(requireActivity(), "Profile clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    private void cargarNuevaImagen(Uri imagenUri) {
        Glide.with(getActivity())
                .load(imagenUri)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(user2);
        Glide.get(getActivity().getApplicationContext()).clearMemory();
    }


    private Uri obtenerNuevaImagenSeleccionada() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String imagenUriStr = sharedPref.getString("imagenUri", null);
        if (imagenUriStr != null) {
            return Uri.parse(imagenUriStr);
        }
        return null;
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        irLogin();
    }

    private void irLogin(){
        Intent intent=new Intent(getActivity(), LRFragmentsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        options.setCircleDimmedLayer(true);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    user2.setImageURI(imageUri);

                    Uri destinoUri = Uri.fromFile(new File(getActivity().getCacheDir(), "imagenRecortada"));
                    UCrop.of(imageUri, destinoUri).withOptions(options).start(getActivity());
                } else {
                    // Puede que haya casos donde la imagenUri sea nula, debes manejarlo adecuadamente
                    Toast.makeText(getActivity(), "Error al obtener la imagen de la cámara", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Manejar el error del recorte
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UCrop", "Error al recortar la imagen", cropError);
                Toast.makeText(getActivity(), "Error al recortar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null) {
            Glide.with(getActivity())
                    .load(resultUri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(user2);

            cambiarFotoPerfil(resultUri);
        } else {
            Toast.makeText(getActivity(), "Error al obtener la imagen recortada", Toast.LENGTH_SHORT).show();
        }
    }



    private void subirFoto(Uri resultUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("perfil/" + userId);

        // Eliminar la foto anterior
        fotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Eliminar foto", "Eliminacion exitosa");

                // Subir la nueva foto después de eliminar la anterior
                UploadTask uploadTask = fotoRef.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fotoUrl = uri.toString();
                                Map<String, Object> datosUsuario = new HashMap<>();
                                datosUsuario.put("foto", fotoUrl);

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null && mFirestore!=null) {
                                    mFirestore.collection("usuarios").document(currentUser.getUid()).update(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getActivity(), "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(Uri.parse(fotoUrl)) // Aquí debes poner la URL de la nueva foto
                                                    .build();

                                            currentUser.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User profile updated.");
                                                            }
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al subir la nueva foto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Eliminar foto", "Error al eliminar foto:" + e.getMessage());
                Toast.makeText(getActivity(), "Error al eliminar foto anterior: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cambiarFotoPerfil(Uri nuevaImagenUri) {
        subirFoto(nuevaImagenUri);
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            guardarImagen(nuevaImagenUri,currentUser.getUid());
        }
    }

    private void guardarImagen(Uri imagenUri,String userId) {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null && currentUser.getUid().equals(userId)) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("imagenUri", imagenUri.toString());
            editor.apply();
        }
    }

    private void loadFirebaseImage(Uri photoUrl){
        Glide.with(getActivity()).load(photoUrl).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
    }

    private void borrarUsuario(){
        FirebaseUser user=mAuth.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                    irLogin();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al eliminar usaurio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CrearNuevoEvento(){
        Intent intent=new Intent(getActivity(), CrearEvento.class);
        startActivity(intent);
    }



}
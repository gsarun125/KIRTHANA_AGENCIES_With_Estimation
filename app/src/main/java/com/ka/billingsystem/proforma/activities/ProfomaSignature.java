package com.ka.billingsystem.proforma.activities;

import static com.ka.billingsystem.utils.ImageEncodeAndDecode.encodeToBase64;
import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.PdfViewActivity;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.databinding.ActivitySignatureBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class ProfomaSignature extends AppCompatActivity {
    private static final String TAG = "Signature";
    private ActivitySignatureBinding binding;
    private DataBaseHandler db = new DataBaseHandler(this);


    private static final int PICK_FILE_REQUEST_CODE = 100;
    private Bitmap sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started","onCreate");
        try {
            super.onCreate(savedInstanceState);
            binding = ActivitySignatureBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            binding.clear.setOnClickListener(v -> {
                binding.signatureView.clearCanvas();
            });

            binding.gen.setOnClickListener(v -> {
                try {
                    if (!binding.signatureView.isBitmapEmpty()) {

                        sign = binding.signatureView.getSignatureBitmap();
                        if (sign != null) {
                            showCustomDialog(sign);
                        }
                    } else {
                        Toast.makeText(this, "Please Draw your signature", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error generating signature", e);
                }
            });

            binding.sigUpload.setOnClickListener(v -> uploadSignature());
            binding.imageButtonUpload.setOnClickListener(v -> uploadSignature());
        } catch (Exception e) {
            Logger.log("Crashed","onCreate");
        }
        Logger.log("Ended","onCreate");
    }

    /**
     * Saves the given bitmap as a Base64-encoded string to SharedPreferences.
     *
     * @param bitmap The bitmap to be saved.
     */
    private void saveToDatabase(Bitmap bitmap) {
        Logger.log("Started"," saveToDatabase");
        try {
            String encodedString = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
            db.ADD_Sgin(encodedString);
            Intent intent = new Intent(this, ProfomaPdfViewActivity.class);
            intent.putExtra("SalesData", getIntent().getStringExtra("SalesData"));
            startActivity(intent);
            super.finish();
        } catch (Exception e) {
            Logger.log("Crashed"," saveToDatabase");
            Log.e(TAG, "Error saving signature to SharedPreferences", e);
        }
        Logger.log("Ended"," saveToDatabase");
    }

    /**
     * Displays a custom dialog with a signature image and an agreement checkbox.
     * Allows the user to save the signature if the agreement checkbox is checked.
     *
     * @param sign The signature bitmap to be displayed in the dialog.
     */
    private void showCustomDialog(Bitmap sign) {
        Logger.log("Started","showCustomDialog");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_signature, null);
            builder.setView(dialogView);

            ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
            CheckBox agreeCheckbox = dialogView.findViewById(R.id.agree_checkbox);

            imageView.setImageBitmap(sign);
            builder.setPositiveButton("OK", null);  // Set to null initially

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(ProfomaSignature.this,positiveButton, negativeButton);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (agreeCheckbox.isChecked()) {
                                dialog.dismiss();
                                saveToDatabase(sign);
                            } else {
                                agreeCheckbox.setError(getString(R.string.please_agree_to_continue));
                            }
                        }
                    });
                }

            });
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed","showCustomDialog");
            Log.e(TAG, "Error showing custom dialog", e);
        }
        Logger.log("Ended","showCustomDialog");
    }

    /**
     * Initiates the process of uploading a signature by launching an intent to open the document.
     */
    private void uploadSignature() {
        Logger.log("Started","uploadSignature");
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } catch (Exception e) {
            Logger.log("Crashed","uploadSignature");
            Log.e(TAG, "selectFile()-An exception occurred", e);
        }
        Logger.log("Ended","uploadSignature");
    }

    /**
     * Starts the CropImage activity for cropping an image.
     *
     * @param sourceUri The Uri of the source image to be cropped.
     */
    private void startCropActivity(Uri sourceUri) {
        Logger.log("Started","startCropActivity");
        try {
            CropImage.activity(sourceUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(900, 300)
                    .setFixAspectRatio(true)
                    .start(this);
        } catch (Exception e) {
            Logger.log("Crashed","startCropActivity");
        }
        Logger.log("Ended","startCropActivity");
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you
     * started it with, the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.log("Started","onActivityResult");
        try {
            if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                // Start the image cropping activity
                startCropActivity(Objects.requireNonNull(uri));
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                // Handle crop result
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    showCustomDialog(croppedBitmap);
                    Toast.makeText(getApplicationContext(), "Image cropped successfully", Toast.LENGTH_SHORT).show();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    // Handle cropping error
                    Toast.makeText(getApplicationContext(), "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);

        } catch (Exception e) {
            Logger.log("Crashed","onActivityResult");
            Log.e(TAG, "onActivityResult()-An exception occurred", e);
        }
        Logger.log("Ended","onActivityResult");
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Back button is disabled", Toast.LENGTH_SHORT).show();
    }
}

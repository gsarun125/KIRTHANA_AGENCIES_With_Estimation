package com.ka.billingsystem.activities;

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

import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.databinding.ActivityEditSignatureBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class EditSignature extends AppCompatActivity {
    private static final String TAG = "DocumentViewActivity";
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private DataBaseHandler db = new DataBaseHandler(this);
    private ActivityEditSignatureBinding binding;
    private Bitmap sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);

            // Inflate the layout
            binding = ActivityEditSignatureBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            binding.backbuttonDocument.setOnClickListener(v -> {
                onBackPressed();
            });
            // Set click listeners for buttons
            binding.clear.setOnClickListener(v -> {
                try {
                    binding.signatureView.clearCanvas();
                } catch (Exception e) {
                    Log.e(TAG, "An exception occurred clearOnClick:", e);
                }
            });

            binding.gen.setOnClickListener(v -> {
                try {
                    if (!binding.signatureView.isBitmapEmpty()) {
                        sign = binding.signatureView.getSignatureBitmap();
                        if (sign != null) {
                            showDialogConfirmation(sign);
                        }
                    } else {
                        Toast.makeText(this, "Please Draw your signature", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "An exception occurred genOnClick:", e);
                }
            });

            binding.sigUpload.setOnClickListener(v -> uploadSignature());
            binding.imageButtonUpload.setOnClickListener(v -> uploadSignature());
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "An exception occurred onCreate:", e);
        }
        Logger.log("Ended", "onCreate");
    }

    /**
     * Save signature to Database
     */
    private void saveToDatabase(Bitmap bitmap) {
        Logger.log("Started", "saveToDatabase");
        try {
            String encodedString = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
            db.ADD_Sgin(encodedString);
            onBackPressed();
        } catch (Exception e) {
            Logger.log("Crashed", "saveToDatabase");
            Log.e(TAG, "An exception occurred saveToSharedPreferences:", e);
        }
        Logger.log("Ended", "saveToDatabase");
    }

    /**
     * Display a custom dialog for user confirmation
     */
    public void showDialogConfirmation(Bitmap sign) {
        Logger.log("Started", "showDialogConfirmation");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_signature, null);
            builder.setView(dialogView);

            ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
            CheckBox agreeCheckbox = dialogView.findViewById(R.id.agree_checkbox);
            builder.setPositiveButton("OK", null);
            imageView.setImageBitmap(sign);
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    Logger.log("Crashed", "showDialogConfirmation");
                    Log.e(TAG, "An exception occurred customDialogNegativeButton:", e);
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(EditSignature.this,positiveButton, negativeButton);
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
            Logger.log("Crashed", "showDialogConfirmation");
            Log.e(TAG, "An exception occurred showCustomDialog:", e);
        }
        Logger.log("Ended", "showDialogConfirmation");
    }

    /**
     * Open file picker for signature upload
     */
    private void uploadSignature() {
        Logger.log("Started", "uploadSignature");
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } catch (Exception e) {
            Logger.log("Crashed", "uploadSignature");
            Log.e(TAG, "An exception occurred uploadSignature:", e);
        }
        Logger.log("Ended", "uploadSignature");
    }

    /**
     * Start image cropping activity
     *
     * @param sourceUri
     */
    private void startCropActivity(Uri sourceUri) {
        Logger.log("Started", "startCropActivity");
        try {
            CropImage.activity(sourceUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(900, 300)
                    .setFixAspectRatio(true)
                    .start(this);
        } catch (Exception e) {
            Logger.log("Crashed", "startCropActivity");
            Log.e(TAG, "An exception occurred startCropActivity:", e);
        }
        Logger.log("Ended", "startCropActivity");
    }

    /**
     * This method is called when the result of an activity started with startActivityForResult is available.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached
     *                    to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.log("Started", "onActivityResult");
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

                    showDialogConfirmation(croppedBitmap);
                    Toast.makeText(getApplicationContext(), "Image cropped successfully", Toast.LENGTH_SHORT).show();
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    // Handle cropping error
                    Toast.makeText(getApplicationContext(), "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);

        } catch (Exception e) {
            Logger.log("Crashed", "onActivityResult");
            Log.e(TAG, "An exception occurred onActivityResult():", e);
        }
        Logger.log("Ended", "onActivityResult");
    }
}

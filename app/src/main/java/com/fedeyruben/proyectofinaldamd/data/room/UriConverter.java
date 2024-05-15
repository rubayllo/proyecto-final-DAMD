package com.fedeyruben.proyectofinaldamd.data.room;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverter {
    @TypeConverter
    public static Uri fromString(String value) {
        return value == null ? null : Uri.parse(value);
    }

    @TypeConverter
    public static String toString(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
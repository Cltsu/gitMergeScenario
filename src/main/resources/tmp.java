package androidx.exifinterface.media;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ExifInterface {
    private static final String TAG = "ExifInterface";
    private static final boolean DEBUG = false;
    public static final String TAG_IMAGE_WIDTH = "ImageWidth";
    public static final String TAG_IMAGE_LENGTH = "ImageLength";
    public static final String TAG_BITS_PER_SAMPLE = "BitsPerSample";
    public static final String TAG_COMPRESSION = "Compression";
    public static final String TAG_PHOTOMETRIC_INTERPRETATION = "PhotometricInterpretation";
    public static final String TAG_ORIENTATION = "Orientation";
    public static final String TAG_SAMPLES_PER_PIXEL = "SamplesPerPixel";
    public static final String TAG_PLANAR_CONFIGURATION = "PlanarConfiguration";
    public static final String TAG_Y_CB_CR_SUB_SAMPLING = "YCbCrSubSampling";
    public static final String TAG_Y_CB_CR_POSITIONING = "YCbCrPositioning";
    public static final String TAG_X_RESOLUTION = "XResolution";
    public static final String TAG_Y_RESOLUTION = "YResolution";
    public static final String TAG_RESOLUTION_UNIT = "ResolutionUnit";
    public static final String TAG_STRIP_OFFSETS = "StripOffsets";
    public static final String TAG_ROWS_PER_STRIP = "RowsPerStrip";
    public static final String TAG_STRIP_BYTE_COUNTS = "StripByteCounts";
    public static final String TAG_JPEG_INTERCHANGE_FORMAT = "JPEGInterchangeFormat";
    public static final String TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = "JPEGInterchangeFormatLength";
    public static final String TAG_TRANSFER_FUNCTION = "TransferFunction";
    public static final String TAG_WHITE_POINT = "WhitePoint";
    public static final String TAG_PRIMARY_CHROMATICITIES = "PrimaryChromaticities";
    public static final String TAG_Y_CB_CR_COEFFICIENTS = "YCbCrCoefficients";
    public static final String TAG_REFERENCE_BLACK_WHITE = "ReferenceBlackWhite";
    public static final String TAG_DATETIME = "DateTime";
    public static final String TAG_IMAGE_DESCRIPTION = "ImageDescription";
    public static final String TAG_MAKE = "Make";
    public static final String TAG_MODEL = "Model";
    public static final String TAG_SOFTWARE = "Software";
    public static final String TAG_ARTIST = "Artist";
    public static final String TAG_COPYRIGHT = "Copyright";
    public static final String TAG_EXIF_VERSION = "ExifVersion";
    public static final String TAG_FLASHPIX_VERSION = "FlashpixVersion";
    public static final String TAG_COLOR_SPACE = "ColorSpace";
    public static final String TAG_GAMMA = "Gamma";
    public static final String TAG_PIXEL_X_DIMENSION = "PixelXDimension";
    public static final String TAG_PIXEL_Y_DIMENSION = "PixelYDimension";
    public static final String TAG_COMPONENTS_CONFIGURATION = "ComponentsConfiguration";
    public static final String TAG_COMPRESSED_BITS_PER_PIXEL = "CompressedBitsPerPixel";
    public static final String TAG_MAKER_NOTE = "MakerNote";
    public static final String TAG_USER_COMMENT = "UserComment";
    public static final String TAG_RELATED_SOUND_FILE = "RelatedSoundFile";
    public static final String TAG_DATETIME_ORIGINAL = "DateTimeOriginal";
    public static final String TAG_DATETIME_DIGITIZED = "DateTimeDigitized";
    public static final String TAG_SUBSEC_TIME = "SubSecTime";
    public static final String TAG_SUBSEC_TIME_ORIGINAL = "SubSecTimeOriginal";
    public static final String TAG_SUBSEC_TIME_DIGITIZED = "SubSecTimeDigitized";
    public static final String TAG_EXPOSURE_TIME = "ExposureTime";
    public static final String TAG_F_NUMBER = "FNumber";
    public static final String TAG_EXPOSURE_PROGRAM = "ExposureProgram";
    public static final String TAG_SPECTRAL_SENSITIVITY = "SpectralSensitivity";
    @Deprecated public static final String TAG_ISO_SPEED_RATINGS = "ISOSpeedRatings";
    public static final String TAG_PHOTOGRAPHIC_SENSITIVITY = "PhotographicSensitivity";
    public static final String TAG_OECF = "OECF";
    public static final String TAG_SENSITIVITY_TYPE = "SensitivityType";
    public static final String TAG_STANDARD_OUTPUT_SENSITIVITY = "StandardOutputSensitivity";
    public static final String TAG_RECOMMENDED_EXPOSURE_INDEX = "RecommendedExposureIndex";
    public static final String TAG_ISO_SPEED = "ISOSpeed";
    public static final String TAG_ISO_SPEED_LATITUDE_YYY = "ISOSpeedLatitudeyyy";
    public static final String TAG_ISO_SPEED_LATITUDE_ZZZ = "ISOSpeedLatitudezzz";
    public static final String TAG_SHUTTER_SPEED_VALUE = "ShutterSpeedValue";
    public static final String TAG_APERTURE_VALUE = "ApertureValue";
    public static final String TAG_BRIGHTNESS_VALUE = "BrightnessValue";
    public static final String TAG_EXPOSURE_BIAS_VALUE = "ExposureBiasValue";
    public static final String TAG_MAX_APERTURE_VALUE = "MaxApertureValue";
    public static final String TAG_SUBJECT_DISTANCE = "SubjectDistance";
    public static final String TAG_METERING_MODE = "MeteringMode";
    public static final String TAG_LIGHT_SOURCE = "LightSource";
    public static final String TAG_FLASH = "Flash";
    public static final String TAG_SUBJECT_AREA = "SubjectArea";
    public static final String TAG_FOCAL_LENGTH = "FocalLength";
    public static final String TAG_FLASH_ENERGY = "FlashEnergy";
    public static final String TAG_SPATIAL_FREQUENCY_RESPONSE = "SpatialFrequencyResponse";
    public static final String TAG_FOCAL_PLANE_X_RESOLUTION = "FocalPlaneXResolution";
    public static final String TAG_FOCAL_PLANE_Y_RESOLUTION = "FocalPlaneYResolution";
    public static final String TAG_FOCAL_PLANE_RESOLUTION_UNIT = "FocalPlaneResolutionUnit";
    public static final String TAG_SUBJECT_LOCATION = "SubjectLocation";
    public static final String TAG_EXPOSURE_INDEX = "ExposureIndex";
    public static final String TAG_SENSING_METHOD = "SensingMethod";
    public static final String TAG_FILE_SOURCE = "FileSource";
    public static final String TAG_SCENE_TYPE = "SceneType";
    public static final String TAG_CFA_PATTERN = "CFAPattern";
    public static final String TAG_CUSTOM_RENDERED = "CustomRendered";
    public static final String TAG_EXPOSURE_MODE = "ExposureMode";
    public static final String TAG_WHITE_BALANCE = "WhiteBalance";
    public static final String TAG_DIGITAL_ZOOM_RATIO = "DigitalZoomRatio";
    public static final String TAG_FOCAL_LENGTH_IN_35MM_FILM = "FocalLengthIn35mmFilm";
    public static final String TAG_SCENE_CAPTURE_TYPE = "SceneCaptureType";
    public static final String TAG_GAIN_CONTROL = "GainControl";
    public static final String TAG_CONTRAST = "Contrast";
    public static final String TAG_SATURATION = "Saturation";
    public static final String TAG_SHARPNESS = "Sharpness";
    public static final String TAG_DEVICE_SETTING_DESCRIPTION = "DeviceSettingDescription";
    public static final String TAG_SUBJECT_DISTANCE_RANGE = "SubjectDistanceRange";
    public static final String TAG_IMAGE_UNIQUE_ID = "ImageUniqueID";
    @Deprecated
    public static final String TAG_CAMARA_OWNER_NAME = "CameraOwnerName";
    public static final String TAG_CAMERA_OWNER_NAME = "CameraOwnerName";
    public static final String TAG_BODY_SERIAL_NUMBER = "BodySerialNumber";
    public static final String TAG_LENS_SPECIFICATION = "LensSpecification";
    public static final String TAG_LENS_MAKE = "LensMake";
    public static final String TAG_LENS_MODEL = "LensModel";
    public static final String TAG_LENS_SERIAL_NUMBER = "LensSerialNumber";
    public static final String TAG_GPS_VERSION_ID = "GPSVersionID";
    public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
    public static final String TAG_GPS_LATITUDE = "GPSLatitude";
    public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
    public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
    public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
    public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
    public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
    public static final String TAG_GPS_SATELLITES = "GPSSatellites";
    public static final String TAG_GPS_STATUS = "GPSStatus";
    public static final String TAG_GPS_MEASURE_MODE = "GPSMeasureMode";
    public static final String TAG_GPS_DOP = "GPSDOP";
    public static final String TAG_GPS_SPEED_REF = "GPSSpeedRef";
    public static final String TAG_GPS_SPEED = "GPSSpeed";
    public static final String TAG_GPS_TRACK_REF = "GPSTrackRef";
    public static final String TAG_GPS_TRACK = "GPSTrack";
    public static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";
    public static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
    public static final String TAG_GPS_MAP_DATUM = "GPSMapDatum";
    public static final String TAG_GPS_DEST_LATITUDE_REF = "GPSDestLatitudeRef";
    public static final String TAG_GPS_DEST_LATITUDE = "GPSDestLatitude";
    public static final String TAG_GPS_DEST_LONGITUDE_REF = "GPSDestLongitudeRef";
    public static final String TAG_GPS_DEST_LONGITUDE = "GPSDestLongitude";
    public static final String TAG_GPS_DEST_BEARING_REF = "GPSDestBearingRef";
    public static final String TAG_GPS_DEST_BEARING = "GPSDestBearing";
    public static final String TAG_GPS_DEST_DISTANCE_REF = "GPSDestDistanceRef";
    public static final String TAG_GPS_DEST_DISTANCE = "GPSDestDistance";
    public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
    public static final String TAG_GPS_AREA_INFORMATION = "GPSAreaInformation";
    public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
    public static final String TAG_GPS_DIFFERENTIAL = "GPSDifferential";
    public static final String TAG_GPS_H_POSITIONING_ERROR = "GPSHPositioningError";
    public static final String TAG_INTEROPERABILITY_INDEX = "InteroperabilityIndex";
    public static final String TAG_THUMBNAIL_IMAGE_LENGTH = "ThumbnailImageLength";
    public static final String TAG_THUMBNAIL_IMAGE_WIDTH = "ThumbnailImageWidth";
    public static final String TAG_DNG_VERSION = "DNGVersion";
    public static final String TAG_DEFAULT_CROP_SIZE = "DefaultCropSize";
    public static final String TAG_ORF_THUMBNAIL_IMAGE = "ThumbnailImage";
    public static final String TAG_ORF_PREVIEW_IMAGE_START = "PreviewImageStart";
    public static final String TAG_ORF_PREVIEW_IMAGE_LENGTH = "PreviewImageLength";
    public static final String TAG_ORF_ASPECT_FRAME = "AspectFrame";
    public static final String TAG_RW2_SENSOR_BOTTOM_BORDER = "SensorBottomBorder";
    public static final String TAG_RW2_SENSOR_LEFT_BORDER = "SensorLeftBorder";
    public static final String TAG_RW2_SENSOR_RIGHT_BORDER = "SensorRightBorder";
    public static final String TAG_RW2_SENSOR_TOP_BORDER = "SensorTopBorder";
    public static final String TAG_RW2_ISO = "ISO";
    public static final String TAG_RW2_JPG_FROM_RAW = "JpgFromRaw";
    public static final String TAG_XMP = "Xmp";
    public static final String TAG_NEW_SUBFILE_TYPE = "NewSubfileType";
    public static final String TAG_SUBFILE_TYPE = "SubfileType";
    private static final String TAG_EXIF_IFD_POINTER = "ExifIFDPointer";
    private static final String TAG_GPS_INFO_IFD_POINTER = "GPSInfoIFDPointer";
    private static final String TAG_INTEROPERABILITY_IFD_POINTER = "InteroperabilityIFDPointer";
    private static final String TAG_SUB_IFD_POINTER = "SubIFDPointer";
    private static final String TAG_ORF_CAMERA_SETTINGS_IFD_POINTER = "CameraSettingsIFDPointer";
    private static final String TAG_ORF_IMAGE_PROCESSING_IFD_POINTER = "ImageProcessingIFDPointer";
    private static final String TAG_HAS_THUMBNAIL = "HasThumbnail";
    private static final String TAG_THUMBNAIL_OFFSET = "ThumbnailOffset";
    private static final String TAG_THUMBNAIL_LENGTH = "ThumbnailLength";
    private static final String TAG_THUMBNAIL_DATA = "ThumbnailData";
    private static final int MAX_THUMBNAIL_SIZE = 512;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_TRANSPOSE = 5;
    public static final int ORIENTATION_ROTATE_90 = 6;
    public static final int ORIENTATION_TRANSVERSE = 7;
    public static final int ORIENTATION_ROTATE_270 = 8;
    private static final List<Integer> ROTATION_ORDER = Arrays.asList(ORIENTATION_NORMAL,
            ORIENTATION_ROTATE_90, ORIENTATION_ROTATE_180, ORIENTATION_ROTATE_270);
    private static final List<Integer> FLIPPED_ROTATION_ORDER = Arrays.asList(
            ORIENTATION_FLIP_HORIZONTAL, ORIENTATION_TRANSVERSE, ORIENTATION_FLIP_VERTICAL,
            ORIENTATION_TRANSPOSE);
    public static final short FORMAT_CHUNKY = 1;
    public static final short FORMAT_PLANAR = 2;
    public static final short Y_CB_CR_POSITIONING_CENTERED = 1;
    public static final short Y_CB_CR_POSITIONING_CO_SITED = 2;
    public static final short RESOLUTION_UNIT_INCHES = 2;
    public static final short RESOLUTION_UNIT_CENTIMETERS = 3;
    public static final int COLOR_SPACE_S_RGB = 1;
    public static final int COLOR_SPACE_UNCALIBRATED = 65535;
    public static final short EXPOSURE_PROGRAM_NOT_DEFINED = 0;
    public static final short EXPOSURE_PROGRAM_MANUAL = 1;
    public static final short EXPOSURE_PROGRAM_NORMAL = 2;
    public static final short EXPOSURE_PROGRAM_APERTURE_PRIORITY = 3;
    public static final short EXPOSURE_PROGRAM_SHUTTER_PRIORITY = 4;
    public static final short EXPOSURE_PROGRAM_CREATIVE = 5;
    public static final short EXPOSURE_PROGRAM_ACTION = 6;
    public static final short EXPOSURE_PROGRAM_PORTRAIT_MODE = 7;
    public static final short EXPOSURE_PROGRAM_LANDSCAPE_MODE = 8;
    public static final short SENSITIVITY_TYPE_UNKNOWN = 0;
    public static final short SENSITIVITY_TYPE_SOS = 1;
    public static final short SENSITIVITY_TYPE_REI = 2;
    public static final short SENSITIVITY_TYPE_ISO_SPEED = 3;
    public static final short SENSITIVITY_TYPE_SOS_AND_REI = 4;
    public static final short SENSITIVITY_TYPE_SOS_AND_ISO = 5;
    public static final short SENSITIVITY_TYPE_REI_AND_ISO = 6;
    public static final short SENSITIVITY_TYPE_SOS_AND_REI_AND_ISO = 7;
    public static final short METERING_MODE_UNKNOWN = 0;
    public static final short METERING_MODE_AVERAGE = 1;
    public static final short METERING_MODE_CENTER_WEIGHT_AVERAGE = 2;
    public static final short METERING_MODE_SPOT = 3;
    public static final short METERING_MODE_MULTI_SPOT = 4;
    public static final short METERING_MODE_PATTERN = 5;
    public static final short METERING_MODE_PARTIAL = 6;
    public static final short METERING_MODE_OTHER = 255;
    public static final short LIGHT_SOURCE_UNKNOWN = 0;
    public static final short LIGHT_SOURCE_DAYLIGHT = 1;
    public static final short LIGHT_SOURCE_FLUORESCENT = 2;
    public static final short LIGHT_SOURCE_TUNGSTEN = 3;
    public static final short LIGHT_SOURCE_FLASH = 4;
    public static final short LIGHT_SOURCE_FINE_WEATHER = 9;
    public static final short LIGHT_SOURCE_CLOUDY_WEATHER = 10;
    public static final short LIGHT_SOURCE_SHADE = 11;
    public static final short LIGHT_SOURCE_DAYLIGHT_FLUORESCENT = 12;
    public static final short LIGHT_SOURCE_DAY_WHITE_FLUORESCENT = 13;
    public static final short LIGHT_SOURCE_COOL_WHITE_FLUORESCENT = 14;
    public static final short LIGHT_SOURCE_WHITE_FLUORESCENT = 15;
    public static final short LIGHT_SOURCE_WARM_WHITE_FLUORESCENT = 16;
    public static final short LIGHT_SOURCE_STANDARD_LIGHT_A = 17;
    public static final short LIGHT_SOURCE_STANDARD_LIGHT_B = 18;
    public static final short LIGHT_SOURCE_STANDARD_LIGHT_C = 19;
    public static final short LIGHT_SOURCE_D55 = 20;
    public static final short LIGHT_SOURCE_D65 = 21;
    public static final short LIGHT_SOURCE_D75 = 22;
    public static final short LIGHT_SOURCE_D50 = 23;
    public static final short LIGHT_SOURCE_ISO_STUDIO_TUNGSTEN = 24;
    public static final short LIGHT_SOURCE_OTHER = 255;
    public static final short FLAG_FLASH_FIRED = 0b0000_0001;
    public static final short FLAG_FLASH_RETURN_LIGHT_NOT_DETECTED = 0b0000_0100;
    public static final short FLAG_FLASH_RETURN_LIGHT_DETECTED = 0b0000_0110;
    public static final short FLAG_FLASH_MODE_COMPULSORY_FIRING = 0b0000_1000;
    public static final short FLAG_FLASH_MODE_COMPULSORY_SUPPRESSION = 0b0001_0000;
    public static final short FLAG_FLASH_MODE_AUTO = 0b0001_1000;
    public static final short FLAG_FLASH_NO_FLASH_FUNCTION = 0b0010_0000;
    public static final short FLAG_FLASH_RED_EYE_SUPPORTED = 0b0100_0000;
    public static final short SENSOR_TYPE_NOT_DEFINED = 1;
    public static final short SENSOR_TYPE_ONE_CHIP = 2;
    public static final short SENSOR_TYPE_TWO_CHIP = 3;
    public static final short SENSOR_TYPE_THREE_CHIP = 4;
    public static final short SENSOR_TYPE_COLOR_SEQUENTIAL = 5;
    public static final short SENSOR_TYPE_TRILINEAR = 7;
    public static final short SENSOR_TYPE_COLOR_SEQUENTIAL_LINEAR = 8;
    public static final short FILE_SOURCE_OTHER = 0;
    public static final short FILE_SOURCE_TRANSPARENT_SCANNER = 1;
    public static final short FILE_SOURCE_REFLEX_SCANNER = 2;
    public static final short FILE_SOURCE_DSC = 3;
    public static final short SCENE_TYPE_DIRECTLY_PHOTOGRAPHED = 1;
    public static final short RENDERED_PROCESS_NORMAL = 0;
    public static final short RENDERED_PROCESS_CUSTOM = 1;
    public static final short EXPOSURE_MODE_AUTO = 0;
    public static final short EXPOSURE_MODE_MANUAL = 1;
    public static final short EXPOSURE_MODE_AUTO_BRACKET = 2;
    @Deprecated public static final int WHITEBALANCE_AUTO = 0;
    @Deprecated public static final int WHITEBALANCE_MANUAL = 1;
    public static final short WHITE_BALANCE_AUTO = 0;
    public static final short WHITE_BALANCE_MANUAL = 1;
    public static final short SCENE_CAPTURE_TYPE_STANDARD = 0;
    public static final short SCENE_CAPTURE_TYPE_LANDSCAPE = 1;
    public static final short SCENE_CAPTURE_TYPE_PORTRAIT = 2;
    public static final short SCENE_CAPTURE_TYPE_NIGHT = 3;
    public static final short GAIN_CONTROL_NONE = 0;
    public static final short GAIN_CONTROL_LOW_GAIN_UP = 1;
    public static final short GAIN_CONTROL_HIGH_GAIN_UP = 2;
    public static final short GAIN_CONTROL_LOW_GAIN_DOWN = 3;
    public static final short GAIN_CONTROL_HIGH_GAIN_DOWN = 4;
    public static final short CONTRAST_NORMAL = 0;
    public static final short CONTRAST_SOFT = 1;
    public static final short CONTRAST_HARD = 2;
    public static final short SATURATION_NORMAL = 0;
    public static final short SATURATION_LOW = 0;
    public static final short SATURATION_HIGH = 0;
    public static final short SHARPNESS_NORMAL = 0;
    public static final short SHARPNESS_SOFT = 1;
    public static final short SHARPNESS_HARD = 2;
    public static final short SUBJECT_DISTANCE_RANGE_UNKNOWN = 0;
    public static final short SUBJECT_DISTANCE_RANGE_MACRO = 1;
    public static final short SUBJECT_DISTANCE_RANGE_CLOSE_VIEW = 2;
    public static final short SUBJECT_DISTANCE_RANGE_DISTANT_VIEW = 3;
    public static final String LATITUDE_NORTH = "N";
    public static final String LATITUDE_SOUTH = "S";
    public static final String LONGITUDE_EAST = "E";
    public static final String LONGITUDE_WEST = "W";
    public static final short ALTITUDE_ABOVE_SEA_LEVEL = 0;
    public static final short ALTITUDE_BELOW_SEA_LEVEL = 1;
    public static final String GPS_MEASUREMENT_IN_PROGRESS = "A";
    public static final String GPS_MEASUREMENT_INTERRUPTED = "V";
    public static final String GPS_MEASUREMENT_2D = "2";
    public static final String GPS_MEASUREMENT_3D = "3";
    public static final String GPS_SPEED_KILOMETERS_PER_HOUR = "K";
    public static final String GPS_SPEED_MILES_PER_HOUR = "M";
    public static final String GPS_SPEED_KNOTS = "N";
    public static final String GPS_DIRECTION_TRUE = "T";
    public static final String GPS_DIRECTION_MAGNETIC = "M";
    public static final String GPS_DISTANCE_KILOMETERS = "K";
    public static final String GPS_DISTANCE_MILES = "M";
    public static final String GPS_DISTANCE_NAUTICAL_MILES = "N";
    public static final short GPS_MEASUREMENT_NO_DIFFERENTIAL = 0;
    public static final short GPS_MEASUREMENT_DIFFERENTIAL_CORRECTED = 1;
    public static final int DATA_UNCOMPRESSED = 1;
    public static final int DATA_HUFFMAN_COMPRESSED = 2;
    public static final int DATA_JPEG = 6;
    public static final int DATA_JPEG_COMPRESSED = 7;
    public static final int DATA_DEFLATE_ZIP = 8;
    public static final int DATA_PACK_BITS_COMPRESSED = 32773;
    public static final int DATA_LOSSY_JPEG = 34892;
    public static final int[] BITS_PER_SAMPLE_RGB = new int[] { 8, 8, 8 };
    public static final int[] BITS_PER_SAMPLE_GREYSCALE_1 = new int[] { 4 };
    public static final int[] BITS_PER_SAMPLE_GREYSCALE_2 = new int[] { 8 };
    public static final int PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO = 0;
    public static final int PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO = 1;
    public static final int PHOTOMETRIC_INTERPRETATION_RGB = 2;
    public static final int PHOTOMETRIC_INTERPRETATION_YCBCR = 6;
    public static final int ORIGINAL_RESOLUTION_IMAGE = 0;
    public static final int REDUCED_RESOLUTION_IMAGE = 1;
    private static final int SIGNATURE_CHECK_SIZE = 5000;
    static final byte[] JPEG_SIGNATURE = new byte[] {(byte) 0xff, (byte) 0xd8, (byte) 0xff};
    private static final String RAF_SIGNATURE = "FUJIFILMCCD-RAW";
    private static final int RAF_OFFSET_TO_JPEG_IMAGE_OFFSET = 84;
    private static final int RAF_INFO_SIZE = 160;
    private static final int RAF_JPEG_LENGTH_VALUE_SIZE = 4;
    private static final short ORF_SIGNATURE_1 = 0x4f52;
    private static final short ORF_SIGNATURE_2 = 0x5352;
    private static final byte[] ORF_MAKER_NOTE_HEADER_1 = new byte[] {(byte) 0x4f, (byte) 0x4c,
            (byte) 0x59, (byte) 0x4d, (byte) 0x50, (byte) 0x00}; // "OLYMP\0"
    private static final byte[] ORF_MAKER_NOTE_HEADER_2 = new byte[] {(byte) 0x4f, (byte) 0x4c,
            (byte) 0x59, (byte) 0x4d, (byte) 0x50, (byte) 0x55, (byte) 0x53, (byte) 0x00,
            (byte) 0x49, (byte) 0x49}; // "OLYMPUS\0II"
    private static final int ORF_MAKER_NOTE_HEADER_1_SIZE = 8;
    private static final int ORF_MAKER_NOTE_HEADER_2_SIZE = 12;
    private static final short RW2_SIGNATURE = 0x0055;
    private static final String PEF_SIGNATURE = "PENTAX";
    private static final int PEF_MAKER_NOTE_SKIP_SIZE = 6;
    private static SimpleDateFormat sFormatter;
    static final short BYTE_ALIGN_II = 0x4949;  // II: Intel order
    static final short BYTE_ALIGN_MM = 0x4d4d;  // MM: Motorola order
    static final byte START_CODE = 0x2a; // 42
    private static final int IFD_OFFSET = 8;
    private static final int IFD_FORMAT_BYTE = 1;
    private static final int IFD_FORMAT_STRING = 2;
    private static final int IFD_FORMAT_USHORT = 3;
    private static final int IFD_FORMAT_ULONG = 4;
    private static final int IFD_FORMAT_URATIONAL = 5;
    private static final int IFD_FORMAT_SBYTE = 6;
    private static final int IFD_FORMAT_UNDEFINED = 7;
    private static final int IFD_FORMAT_SSHORT = 8;
    private static final int IFD_FORMAT_SLONG = 9;
    private static final int IFD_FORMAT_SRATIONAL = 10;
    private static final int IFD_FORMAT_SINGLE = 11;
    private static final int IFD_FORMAT_DOUBLE = 12;
    private static final int IFD_FORMAT_IFD = 13;
    static final String[] IFD_FORMAT_NAMES = new String[] {
            "", "BYTE", "STRING", "USHORT", "ULONG", "URATIONAL", "SBYTE", "UNDEFINED", "SSHORT",
            "SLONG", "SRATIONAL", "SINGLE", "DOUBLE"
    };
    static final int[] IFD_FORMAT_BYTES_PER_FORMAT = new int[] {
            0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 1
    };
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static final byte[] EXIF_ASCII_PREFIX = new byte[] {
            0x41, 0x53, 0x43, 0x49, 0x49, 0x0, 0x0, 0x0
    };
    private static class Rational {
        public final long numerator;
        public final long denominator;
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        Rational(double value) {
            this((long) (value * 10000), 10000);
        }
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        Rational(long numerator, long denominator) {
            if (denominator == 0) {
                this.numerator = 0;
                this.denominator = 1;
                return;
            }
            this.numerator = numerator;
            this.denominator = denominator;
        }
        @Override
        public String toString() {
            return numerator + "/" + denominator;
        }
        public double calculate() {
            return (double) numerator / denominator;
        }
    }
    private static class ExifAttribute {
        public static final long BYTES_OFFSET_UNKNOWN = -1;
        public final int format;
        public final int numberOfComponents;
        public final long bytesOffset;
        public final byte[] bytes;
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        ExifAttribute(int format, int numberOfComponents, byte[] bytes) {
            this(format, numberOfComponents, BYTES_OFFSET_UNKNOWN, bytes);
        }
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        ExifAttribute(int format, int numberOfComponents, long bytesOffset, byte[] bytes) {
            this.format = format;
            this.numberOfComponents = numberOfComponents;
            this.bytesOffset = bytesOffset;
            this.bytes = bytes;
        }
        public static ExifAttribute createUShort(int[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_USHORT] * values.length]);
            buffer.order(byteOrder);
            for (int value : values) {
                buffer.putShort((short) value);
            }
            return new ExifAttribute(IFD_FORMAT_USHORT, values.length, buffer.array());
        }
        public static ExifAttribute createUShort(int value, ByteOrder byteOrder) {
            return createUShort(new int[] {value}, byteOrder);
        }
        public static ExifAttribute createULong(long[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_ULONG] * values.length]);
            buffer.order(byteOrder);
            for (long value : values) {
                buffer.putInt((int) value);
            }
            return new ExifAttribute(IFD_FORMAT_ULONG, values.length, buffer.array());
        }
        public static ExifAttribute createULong(long value, ByteOrder byteOrder) {
            return createULong(new long[] {value}, byteOrder);
        }
        public static ExifAttribute createSLong(int[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_SLONG] * values.length]);
            buffer.order(byteOrder);
            for (int value : values) {
                buffer.putInt(value);
            }
            return new ExifAttribute(IFD_FORMAT_SLONG, values.length, buffer.array());
        }
        public static ExifAttribute createSLong(int value, ByteOrder byteOrder) {
            return createSLong(new int[] {value}, byteOrder);
        }
        public static ExifAttribute createByte(String value) {
            if (value.length() == 1 && value.charAt(0) >= '0' && value.charAt(0) <= '1') {
                final byte[] bytes = new byte[] { (byte) (value.charAt(0) - '0') };
                return new ExifAttribute(IFD_FORMAT_BYTE, bytes.length, bytes);
            }
            final byte[] ascii = value.getBytes(ASCII);
            return new ExifAttribute(IFD_FORMAT_BYTE, ascii.length, ascii);
        }
        public static ExifAttribute createString(String value) {
            final byte[] ascii = (value + '\0').getBytes(ASCII);
            return new ExifAttribute(IFD_FORMAT_STRING, ascii.length, ascii);
        }
        public static ExifAttribute createURational(Rational[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_URATIONAL] * values.length]);
            buffer.order(byteOrder);
            for (Rational value : values) {
                buffer.putInt((int) value.numerator);
                buffer.putInt((int) value.denominator);
            }
            return new ExifAttribute(IFD_FORMAT_URATIONAL, values.length, buffer.array());
        }
        public static ExifAttribute createURational(Rational value, ByteOrder byteOrder) {
            return createURational(new Rational[] {value}, byteOrder);
        }
        public static ExifAttribute createSRational(Rational[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_SRATIONAL] * values.length]);
            buffer.order(byteOrder);
            for (Rational value : values) {
                buffer.putInt((int) value.numerator);
                buffer.putInt((int) value.denominator);
            }
            return new ExifAttribute(IFD_FORMAT_SRATIONAL, values.length, buffer.array());
        }
        public static ExifAttribute createSRational(Rational value, ByteOrder byteOrder) {
            return createSRational(new Rational[] {value}, byteOrder);
        }
        public static ExifAttribute createDouble(double[] values, ByteOrder byteOrder) {
            final ByteBuffer buffer = ByteBuffer.wrap(
                    new byte[IFD_FORMAT_BYTES_PER_FORMAT[IFD_FORMAT_DOUBLE] * values.length]);
            buffer.order(byteOrder);
            for (double value : values) {
                buffer.putDouble(value);
            }
            return new ExifAttribute(IFD_FORMAT_DOUBLE, values.length, buffer.array());
        }
        public static ExifAttribute createDouble(double value, ByteOrder byteOrder) {
            return createDouble(new double[] {value}, byteOrder);
        }
        @Override
        public String toString() {
            return "(" + IFD_FORMAT_NAMES[format] + ", data length:" + bytes.length + ")";
        }
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        Object getValue(ByteOrder byteOrder) {
            ByteOrderedDataInputStream inputStream = null;
            try {
                inputStream = new ByteOrderedDataInputStream(bytes);
                inputStream.setByteOrder(byteOrder);
                switch (format) {
                    case IFD_FORMAT_BYTE:
                    case IFD_FORMAT_SBYTE: {
                        if (bytes.length == 1 && bytes[0] >= 0 && bytes[0] <= 1) {
                            return new String(new char[] { (char) (bytes[0] + '0') });
                        }
                        return new String(bytes, ASCII);
                    }
                    case IFD_FORMAT_UNDEFINED:
                    case IFD_FORMAT_STRING: {
                        int index = 0;
                        if (numberOfComponents >= EXIF_ASCII_PREFIX.length) {
                            boolean same = true;
                            for (int i = 0; i < EXIF_ASCII_PREFIX.length; ++i) {
                                if (bytes[i] != EXIF_ASCII_PREFIX[i]) {
                                    same = false;
                                    break;
                                }
                            }
                            if (same) {
                                index = EXIF_ASCII_PREFIX.length;
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        while (index < numberOfComponents) {
                            int ch = bytes[index];
                            if (ch == 0) {
                                break;
                            }
                            if (ch >= 32) {
                                stringBuilder.append((char) ch);
                            } else {
                                stringBuilder.append('?');
                            }
                            ++index;
                        }
                        return stringBuilder.toString();
                    }
                    case IFD_FORMAT_USHORT: {
                        final int[] values = new int[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readUnsignedShort();
                        }
                        return values;
                    }
                    case IFD_FORMAT_ULONG: {
                        final long[] values = new long[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readUnsignedInt();
                        }
                        return values;
                    }
                    case IFD_FORMAT_URATIONAL: {
                        final Rational[] values = new Rational[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            final long numerator = inputStream.readUnsignedInt();
                            final long denominator = inputStream.readUnsignedInt();
                            values[i] = new Rational(numerator, denominator);
                        }
                        return values;
                    }
                    case IFD_FORMAT_SSHORT: {
                        final int[] values = new int[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readShort();
                        }
                        return values;
                    }
                    case IFD_FORMAT_SLONG: {
                        final int[] values = new int[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readInt();
                        }
                        return values;
                    }
                    case IFD_FORMAT_SRATIONAL: {
                        final Rational[] values = new Rational[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            final long numerator = inputStream.readInt();
                            final long denominator = inputStream.readInt();
                            values[i] = new Rational(numerator, denominator);
                        }
                        return values;
                    }
                    case IFD_FORMAT_SINGLE: {
                        final double[] values = new double[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readFloat();
                        }
                        return values;
                    }
                    case IFD_FORMAT_DOUBLE: {
                        final double[] values = new double[numberOfComponents];
                        for (int i = 0; i < numberOfComponents; ++i) {
                            values[i] = inputStream.readDouble();
                        }
                        return values;
                    }
                    default:
                        return null;
                }
            } catch (IOException e) {
                Log.w(TAG, "IOException occurred during reading a value", e);
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException occurred while closing InputStream", e);
                    }
                }
            }
        }
        public double getDoubleValue(ByteOrder byteOrder) {
            Object value = getValue(byteOrder);
            if (value == null) {
                throw new NumberFormatException("NULL can't be converted to a double value");
            }
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            if (value instanceof long[]) {
                long[] array = (long[]) value;
                if (array.length == 1) {
                    return array[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof int[]) {
                int[] array = (int[]) value;
                if (array.length == 1) {
                    return array[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof double[]) {
                double[] array = (double[]) value;
                if (array.length == 1) {
                    return array[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof Rational[]) {
                Rational[] array = (Rational[]) value;
                if (array.length == 1) {
                    return array[0].calculate();
                }
                throw new NumberFormatException("There are more than one component");
            }
            throw new NumberFormatException("Couldn't find a double value");
        }
        public int getIntValue(ByteOrder byteOrder) {
            Object value = getValue(byteOrder);
            if (value == null) {
                throw new NumberFormatException("NULL can't be converted to a integer value");
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            if (value instanceof long[]) {
                long[] array = (long[]) value;
                if (array.length == 1) {
                    return (int) array[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            if (value instanceof int[]) {
                int[] array = (int[]) value;
                if (array.length == 1) {
                    return array[0];
                }
                throw new NumberFormatException("There are more than one component");
            }
            throw new NumberFormatException("Couldn't find a integer value");
        }
        public String getStringValue(ByteOrder byteOrder) {
            Object value = getValue(byteOrder);
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (String) value;
            }
            final StringBuilder stringBuilder = new StringBuilder();
            if (value instanceof long[]) {
                long[] array = (long[]) value;
                for (int i = 0; i < array.length; ++i) {
                    stringBuilder.append(array[i]);
                    if (i + 1 != array.length) {
                        stringBuilder.append(",");
                    }
                }
                return stringBuilder.toString();
            }
            if (value instanceof int[]) {
                int[] array = (int[]) value;
                for (int i = 0; i < array.length; ++i) {
                    stringBuilder.append(array[i]);
                    if (i + 1 != array.length) {
                        stringBuilder.append(",");
                    }
                }
                return stringBuilder.toString();
            }
            if (value instanceof double[]) {
                double[] array = (double[]) value;
                for (int i = 0; i < array.length; ++i) {
                    stringBuilder.append(array[i]);
                    if (i + 1 != array.length) {
                        stringBuilder.append(",");
                    }
                }
                return stringBuilder.toString();
            }
            if (value instanceof Rational[]) {
                Rational[] array = (Rational[]) value;
                for (int i = 0; i < array.length; ++i) {
                    stringBuilder.append(array[i].numerator);
                    stringBuilder.append('/');
                    stringBuilder.append(array[i].denominator);
                    if (i + 1 != array.length) {
                        stringBuilder.append(",");
                    }
                }
                return stringBuilder.toString();
            }
            return null;
        }
        public int size() {
            return IFD_FORMAT_BYTES_PER_FORMAT[format] * numberOfComponents;
        }
    }
    static class ExifTag {
        public final int number;
        public final String name;
        public final int primaryFormat;
        public final int secondaryFormat;
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        ExifTag(String name, int number, int format) {
            this.name = name;
            this.number = number;
            this.primaryFormat = format;
            this.secondaryFormat = -1;
        }
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        ExifTag(String name, int number, int primaryFormat, int secondaryFormat) {
            this.name = name;
            this.number = number;
            this.primaryFormat = primaryFormat;
            this.secondaryFormat = secondaryFormat;
        }
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        boolean isFormatCompatible(int format) {
            if (primaryFormat == IFD_FORMAT_UNDEFINED || format == IFD_FORMAT_UNDEFINED) {
                return true;
            } else if (primaryFormat == format || secondaryFormat == format) {
                return true;
            } else if ((primaryFormat == IFD_FORMAT_ULONG || secondaryFormat == IFD_FORMAT_ULONG)
                    && format == IFD_FORMAT_USHORT) {
                return true;
            } else if ((primaryFormat == IFD_FORMAT_SLONG || secondaryFormat == IFD_FORMAT_SLONG)
                    && format == IFD_FORMAT_SSHORT) {
                return true;
            } else if ((primaryFormat == IFD_FORMAT_DOUBLE || secondaryFormat == IFD_FORMAT_DOUBLE)
                    && format == IFD_FORMAT_SINGLE) {
                return true;
            }
            return false;
        }
    }
    private static final ExifTag[] IFD_TIFF_TAGS = new ExifTag[] {
            new ExifTag(TAG_NEW_SUBFILE_TYPE, 254, IFD_FORMAT_ULONG),
            new ExifTag(TAG_SUBFILE_TYPE, 255, IFD_FORMAT_ULONG),
            new ExifTag(TAG_IMAGE_WIDTH, 256, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_IMAGE_LENGTH, 257, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_BITS_PER_SAMPLE, 258, IFD_FORMAT_USHORT),
            new ExifTag(TAG_COMPRESSION, 259, IFD_FORMAT_USHORT),
            new ExifTag(TAG_PHOTOMETRIC_INTERPRETATION, 262, IFD_FORMAT_USHORT),
            new ExifTag(TAG_IMAGE_DESCRIPTION, 270, IFD_FORMAT_STRING),
            new ExifTag(TAG_MAKE, 271, IFD_FORMAT_STRING),
            new ExifTag(TAG_MODEL, 272, IFD_FORMAT_STRING),
            new ExifTag(TAG_STRIP_OFFSETS, 273, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_ORIENTATION, 274, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SAMPLES_PER_PIXEL, 277, IFD_FORMAT_USHORT),
            new ExifTag(TAG_ROWS_PER_STRIP, 278, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_STRIP_BYTE_COUNTS, 279, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_X_RESOLUTION, 282, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_Y_RESOLUTION, 283, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_PLANAR_CONFIGURATION, 284, IFD_FORMAT_USHORT),
            new ExifTag(TAG_RESOLUTION_UNIT, 296, IFD_FORMAT_USHORT),
            new ExifTag(TAG_TRANSFER_FUNCTION, 301, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SOFTWARE, 305, IFD_FORMAT_STRING),
            new ExifTag(TAG_DATETIME, 306, IFD_FORMAT_STRING),
            new ExifTag(TAG_ARTIST, 315, IFD_FORMAT_STRING),
            new ExifTag(TAG_WHITE_POINT, 318, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_PRIMARY_CHROMATICITIES, 319, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SUB_IFD_POINTER, 330, IFD_FORMAT_ULONG),
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT, 513, IFD_FORMAT_ULONG),
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, 514, IFD_FORMAT_ULONG),
            new ExifTag(TAG_Y_CB_CR_COEFFICIENTS, 529, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_Y_CB_CR_SUB_SAMPLING, 530, IFD_FORMAT_USHORT),
            new ExifTag(TAG_Y_CB_CR_POSITIONING, 531, IFD_FORMAT_USHORT),
            new ExifTag(TAG_REFERENCE_BLACK_WHITE, 532, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_COPYRIGHT, 33432, IFD_FORMAT_STRING),
            new ExifTag(TAG_EXIF_IFD_POINTER, 34665, IFD_FORMAT_ULONG),
            new ExifTag(TAG_GPS_INFO_IFD_POINTER, 34853, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RW2_SENSOR_TOP_BORDER, 4, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RW2_SENSOR_LEFT_BORDER, 5, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RW2_SENSOR_BOTTOM_BORDER, 6, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RW2_SENSOR_RIGHT_BORDER, 7, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RW2_ISO, 23, IFD_FORMAT_USHORT),
            new ExifTag(TAG_RW2_JPG_FROM_RAW, 46, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_XMP, 700, IFD_FORMAT_BYTE),
    };
    private static final ExifTag[] IFD_EXIF_TAGS = new ExifTag[] {
            new ExifTag(TAG_EXPOSURE_TIME, 33434, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_F_NUMBER, 33437, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_EXPOSURE_PROGRAM, 34850, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SPECTRAL_SENSITIVITY, 34852, IFD_FORMAT_STRING),
            new ExifTag(TAG_PHOTOGRAPHIC_SENSITIVITY, 34855, IFD_FORMAT_USHORT),
            new ExifTag(TAG_OECF, 34856, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_EXIF_VERSION, 36864, IFD_FORMAT_STRING),
            new ExifTag(TAG_DATETIME_ORIGINAL, 36867, IFD_FORMAT_STRING),
            new ExifTag(TAG_DATETIME_DIGITIZED, 36868, IFD_FORMAT_STRING),
            new ExifTag(TAG_COMPONENTS_CONFIGURATION, 37121, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_COMPRESSED_BITS_PER_PIXEL, 37122, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SHUTTER_SPEED_VALUE, 37377, IFD_FORMAT_SRATIONAL),
            new ExifTag(TAG_APERTURE_VALUE, 37378, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_BRIGHTNESS_VALUE, 37379, IFD_FORMAT_SRATIONAL),
            new ExifTag(TAG_EXPOSURE_BIAS_VALUE, 37380, IFD_FORMAT_SRATIONAL),
            new ExifTag(TAG_MAX_APERTURE_VALUE, 37381, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SUBJECT_DISTANCE, 37382, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_METERING_MODE, 37383, IFD_FORMAT_USHORT),
            new ExifTag(TAG_LIGHT_SOURCE, 37384, IFD_FORMAT_USHORT),
            new ExifTag(TAG_FLASH, 37385, IFD_FORMAT_USHORT),
            new ExifTag(TAG_FOCAL_LENGTH, 37386, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SUBJECT_AREA, 37396, IFD_FORMAT_USHORT),
            new ExifTag(TAG_MAKER_NOTE, 37500, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_USER_COMMENT, 37510, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_SUBSEC_TIME, 37520, IFD_FORMAT_STRING),
            new ExifTag(TAG_SUBSEC_TIME_ORIGINAL, 37521, IFD_FORMAT_STRING),
            new ExifTag(TAG_SUBSEC_TIME_DIGITIZED, 37522, IFD_FORMAT_STRING),
            new ExifTag(TAG_FLASHPIX_VERSION, 40960, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_COLOR_SPACE, 40961, IFD_FORMAT_USHORT),
            new ExifTag(TAG_PIXEL_X_DIMENSION, 40962, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_PIXEL_Y_DIMENSION, 40963, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_RELATED_SOUND_FILE, 40964, IFD_FORMAT_STRING),
            new ExifTag(TAG_INTEROPERABILITY_IFD_POINTER, 40965, IFD_FORMAT_ULONG),
            new ExifTag(TAG_FLASH_ENERGY, 41483, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SPATIAL_FREQUENCY_RESPONSE, 41484, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_FOCAL_PLANE_X_RESOLUTION, 41486, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_FOCAL_PLANE_Y_RESOLUTION, 41487, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_FOCAL_PLANE_RESOLUTION_UNIT, 41488, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SUBJECT_LOCATION, 41492, IFD_FORMAT_USHORT),
            new ExifTag(TAG_EXPOSURE_INDEX, 41493, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SENSING_METHOD, 41495, IFD_FORMAT_USHORT),
            new ExifTag(TAG_FILE_SOURCE, 41728, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_SCENE_TYPE, 41729, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_CFA_PATTERN, 41730, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_CUSTOM_RENDERED, 41985, IFD_FORMAT_USHORT),
            new ExifTag(TAG_EXPOSURE_MODE, 41986, IFD_FORMAT_USHORT),
            new ExifTag(TAG_WHITE_BALANCE, 41987, IFD_FORMAT_USHORT),
            new ExifTag(TAG_DIGITAL_ZOOM_RATIO, 41988, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_FOCAL_LENGTH_IN_35MM_FILM, 41989, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SCENE_CAPTURE_TYPE, 41990, IFD_FORMAT_USHORT),
            new ExifTag(TAG_GAIN_CONTROL, 41991, IFD_FORMAT_USHORT),
            new ExifTag(TAG_CONTRAST, 41992, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SATURATION, 41993, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SHARPNESS, 41994, IFD_FORMAT_USHORT),
            new ExifTag(TAG_DEVICE_SETTING_DESCRIPTION, 41995, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_SUBJECT_DISTANCE_RANGE, 41996, IFD_FORMAT_USHORT),
            new ExifTag(TAG_IMAGE_UNIQUE_ID, 42016, IFD_FORMAT_STRING),
            new ExifTag(TAG_DNG_VERSION, 50706, IFD_FORMAT_BYTE),
            new ExifTag(TAG_DEFAULT_CROP_SIZE, 50720, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG)
    };
    private static final ExifTag[] IFD_GPS_TAGS = new ExifTag[] {
            new ExifTag(TAG_GPS_VERSION_ID, 0, IFD_FORMAT_BYTE),
            new ExifTag(TAG_GPS_LATITUDE_REF, 1, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_LATITUDE, 2, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_LONGITUDE_REF, 3, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_LONGITUDE, 4, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_ALTITUDE_REF, 5, IFD_FORMAT_BYTE),
            new ExifTag(TAG_GPS_ALTITUDE, 6, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_TIMESTAMP, 7, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_SATELLITES, 8, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_STATUS, 9, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_MEASURE_MODE, 10, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DOP, 11, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_SPEED_REF, 12, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_SPEED, 13, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_TRACK_REF, 14, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_TRACK, 15, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_IMG_DIRECTION_REF, 16, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_IMG_DIRECTION, 17, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_MAP_DATUM, 18, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DEST_LATITUDE_REF, 19, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DEST_LATITUDE, 20, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_DEST_LONGITUDE_REF, 21, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DEST_LONGITUDE, 22, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_DEST_BEARING_REF, 23, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DEST_BEARING, 24, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_DEST_DISTANCE_REF, 25, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DEST_DISTANCE, 26, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_GPS_PROCESSING_METHOD, 27, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_GPS_AREA_INFORMATION, 28, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_GPS_DATESTAMP, 29, IFD_FORMAT_STRING),
            new ExifTag(TAG_GPS_DIFFERENTIAL, 30, IFD_FORMAT_USHORT)
    };
    private static final ExifTag[] IFD_INTEROPERABILITY_TAGS = new ExifTag[] {
            new ExifTag(TAG_INTEROPERABILITY_INDEX, 1, IFD_FORMAT_STRING)
    };
    private static final ExifTag[] IFD_THUMBNAIL_TAGS = new ExifTag[] {
            new ExifTag(TAG_NEW_SUBFILE_TYPE, 254, IFD_FORMAT_ULONG),
            new ExifTag(TAG_SUBFILE_TYPE, 255, IFD_FORMAT_ULONG),
            new ExifTag(TAG_THUMBNAIL_IMAGE_WIDTH, 256, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_THUMBNAIL_IMAGE_LENGTH, 257, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_BITS_PER_SAMPLE, 258, IFD_FORMAT_USHORT),
            new ExifTag(TAG_COMPRESSION, 259, IFD_FORMAT_USHORT),
            new ExifTag(TAG_PHOTOMETRIC_INTERPRETATION, 262, IFD_FORMAT_USHORT),
            new ExifTag(TAG_IMAGE_DESCRIPTION, 270, IFD_FORMAT_STRING),
            new ExifTag(TAG_MAKE, 271, IFD_FORMAT_STRING),
            new ExifTag(TAG_MODEL, 272, IFD_FORMAT_STRING),
            new ExifTag(TAG_STRIP_OFFSETS, 273, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_ORIENTATION, 274, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SAMPLES_PER_PIXEL, 277, IFD_FORMAT_USHORT),
            new ExifTag(TAG_ROWS_PER_STRIP, 278, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_STRIP_BYTE_COUNTS, 279, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG),
            new ExifTag(TAG_X_RESOLUTION, 282, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_Y_RESOLUTION, 283, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_PLANAR_CONFIGURATION, 284, IFD_FORMAT_USHORT),
            new ExifTag(TAG_RESOLUTION_UNIT, 296, IFD_FORMAT_USHORT),
            new ExifTag(TAG_TRANSFER_FUNCTION, 301, IFD_FORMAT_USHORT),
            new ExifTag(TAG_SOFTWARE, 305, IFD_FORMAT_STRING),
            new ExifTag(TAG_DATETIME, 306, IFD_FORMAT_STRING),
            new ExifTag(TAG_ARTIST, 315, IFD_FORMAT_STRING),
            new ExifTag(TAG_WHITE_POINT, 318, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_PRIMARY_CHROMATICITIES, 319, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_SUB_IFD_POINTER, 330, IFD_FORMAT_ULONG),
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT, 513, IFD_FORMAT_ULONG),
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, 514, IFD_FORMAT_ULONG),
            new ExifTag(TAG_Y_CB_CR_COEFFICIENTS, 529, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_Y_CB_CR_SUB_SAMPLING, 530, IFD_FORMAT_USHORT),
            new ExifTag(TAG_Y_CB_CR_POSITIONING, 531, IFD_FORMAT_USHORT),
            new ExifTag(TAG_REFERENCE_BLACK_WHITE, 532, IFD_FORMAT_URATIONAL),
            new ExifTag(TAG_COPYRIGHT, 33432, IFD_FORMAT_STRING),
            new ExifTag(TAG_EXIF_IFD_POINTER, 34665, IFD_FORMAT_ULONG),
            new ExifTag(TAG_GPS_INFO_IFD_POINTER, 34853, IFD_FORMAT_ULONG),
            new ExifTag(TAG_DNG_VERSION, 50706, IFD_FORMAT_BYTE),
            new ExifTag(TAG_DEFAULT_CROP_SIZE, 50720, IFD_FORMAT_USHORT, IFD_FORMAT_ULONG)
    };
    private static final ExifTag TAG_RAF_IMAGE_SIZE =
            new ExifTag(TAG_STRIP_OFFSETS, 273, IFD_FORMAT_USHORT);
    private static final ExifTag[] ORF_MAKER_NOTE_TAGS = new ExifTag[] {
            new ExifTag(TAG_ORF_THUMBNAIL_IMAGE, 256, IFD_FORMAT_UNDEFINED),
            new ExifTag(TAG_ORF_CAMERA_SETTINGS_IFD_POINTER, 8224, IFD_FORMAT_ULONG),
            new ExifTag(TAG_ORF_IMAGE_PROCESSING_IFD_POINTER, 8256, IFD_FORMAT_ULONG)
    };
    private static final ExifTag[] ORF_CAMERA_SETTINGS_TAGS = new ExifTag[] {
            new ExifTag(TAG_ORF_PREVIEW_IMAGE_START, 257, IFD_FORMAT_ULONG),
            new ExifTag(TAG_ORF_PREVIEW_IMAGE_LENGTH, 258, IFD_FORMAT_ULONG)
    };
    private static final ExifTag[] ORF_IMAGE_PROCESSING_TAGS = new ExifTag[] {
            new ExifTag(TAG_ORF_ASPECT_FRAME, 4371, IFD_FORMAT_USHORT)
    };
    private static final ExifTag[] PEF_TAGS = new ExifTag[] {
            new ExifTag(TAG_COLOR_SPACE, 55, IFD_FORMAT_USHORT)
    };
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IFD_TYPE_PRIMARY, IFD_TYPE_EXIF, IFD_TYPE_GPS, IFD_TYPE_INTEROPERABILITY,
            IFD_TYPE_THUMBNAIL, IFD_TYPE_PREVIEW, IFD_TYPE_ORF_MAKER_NOTE,
            IFD_TYPE_ORF_CAMERA_SETTINGS, IFD_TYPE_ORF_IMAGE_PROCESSING, IFD_TYPE_PEF})
    public @interface IfdType {}
    static final int IFD_TYPE_PRIMARY = 0;
    private static final int IFD_TYPE_EXIF = 1;
    private static final int IFD_TYPE_GPS = 2;
    private static final int IFD_TYPE_INTEROPERABILITY = 3;
    static final int IFD_TYPE_THUMBNAIL = 4;
    static final int IFD_TYPE_PREVIEW = 5;
    private static final int IFD_TYPE_ORF_MAKER_NOTE = 6;
    private static final int IFD_TYPE_ORF_CAMERA_SETTINGS = 7;
    private static final int IFD_TYPE_ORF_IMAGE_PROCESSING = 8;
    private static final int IFD_TYPE_PEF = 9;
    static final ExifTag[][] EXIF_TAGS = new ExifTag[][] {
            IFD_TIFF_TAGS, IFD_EXIF_TAGS, IFD_GPS_TAGS, IFD_INTEROPERABILITY_TAGS,
            IFD_THUMBNAIL_TAGS, IFD_TIFF_TAGS, ORF_MAKER_NOTE_TAGS, ORF_CAMERA_SETTINGS_TAGS,
            ORF_IMAGE_PROCESSING_TAGS, PEF_TAGS
    };
    private static final ExifTag[] EXIF_POINTER_TAGS = new ExifTag[] {
            new ExifTag(TAG_SUB_IFD_POINTER, 330, IFD_FORMAT_ULONG),
            new ExifTag(TAG_EXIF_IFD_POINTER, 34665, IFD_FORMAT_ULONG),
            new ExifTag(TAG_GPS_INFO_IFD_POINTER, 34853, IFD_FORMAT_ULONG),
            new ExifTag(TAG_INTEROPERABILITY_IFD_POINTER, 40965, IFD_FORMAT_ULONG),
            new ExifTag(TAG_ORF_CAMERA_SETTINGS_IFD_POINTER, 8224, IFD_FORMAT_BYTE),
            new ExifTag(TAG_ORF_IMAGE_PROCESSING_IFD_POINTER, 8256, IFD_FORMAT_BYTE)
    };
    private static final ExifTag JPEG_INTERCHANGE_FORMAT_TAG =
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT, 513, IFD_FORMAT_ULONG);
    private static final ExifTag JPEG_INTERCHANGE_FORMAT_LENGTH_TAG =
            new ExifTag(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, 514, IFD_FORMAT_ULONG);
    @SuppressWarnings("unchecked")
    private static final HashMap<Integer, ExifTag>[] sExifTagMapsForReading =
            new HashMap[EXIF_TAGS.length];
    @SuppressWarnings("unchecked")
    private static final HashMap<String, ExifTag>[] sExifTagMapsForWriting =
            new HashMap[EXIF_TAGS.length];
    private static final HashSet<String> sTagSetForCompatibility = new HashSet<>(Arrays.asList(
            TAG_F_NUMBER, TAG_DIGITAL_ZOOM_RATIO, TAG_EXPOSURE_TIME, TAG_SUBJECT_DISTANCE,
            TAG_GPS_TIMESTAMP));
    @SuppressWarnings("unchecked")
    private static final HashMap<Integer, Integer> sExifPointerTagMap = new HashMap();
    @SuppressWarnings("WeakerAccess") /* synthetic access */
    static final Charset ASCII = Charset.forName("US-ASCII");
    static final byte[] IDENTIFIER_EXIF_APP1 = "Exif\0\0".getBytes(ASCII);
    private static final byte[] IDENTIFIER_XMP_APP1 =
            "http://ns.adobe.com/xap/1.0/\0".getBytes(ASCII);
    static final byte MARKER = (byte) 0xff;
    private static final byte MARKER_SOI = (byte) 0xd8;
    private static final byte MARKER_SOF0 = (byte) 0xc0;
    private static final byte MARKER_SOF1 = (byte) 0xc1;
    private static final byte MARKER_SOF2 = (byte) 0xc2;
    private static final byte MARKER_SOF3 = (byte) 0xc3;
    private static final byte MARKER_SOF5 = (byte) 0xc5;
    private static final byte MARKER_SOF6 = (byte) 0xc6;
    private static final byte MARKER_SOF7 = (byte) 0xc7;
    private static final byte MARKER_SOF9 = (byte) 0xc9;
    private static final byte MARKER_SOF10 = (byte) 0xca;
    private static final byte MARKER_SOF11 = (byte) 0xcb;
    private static final byte MARKER_SOF13 = (byte) 0xcd;
    private static final byte MARKER_SOF14 = (byte) 0xce;
    private static final byte MARKER_SOF15 = (byte) 0xcf;
    private static final byte MARKER_SOS = (byte) 0xda;
    static final byte MARKER_APP1 = (byte) 0xe1;
    private static final byte MARKER_COM = (byte) 0xfe;
    static final byte MARKER_EOI = (byte) 0xd9;
    private static final int IMAGE_TYPE_UNKNOWN = 0;
    private static final int IMAGE_TYPE_ARW = 1;
    private static final int IMAGE_TYPE_CR2 = 2;
    private static final int IMAGE_TYPE_DNG = 3;
    private static final int IMAGE_TYPE_JPEG = 4;
    private static final int IMAGE_TYPE_NEF = 5;
    private static final int IMAGE_TYPE_NRW = 6;
    private static final int IMAGE_TYPE_ORF = 7;
    private static final int IMAGE_TYPE_PEF = 8;
    private static final int IMAGE_TYPE_RAF = 9;
    private static final int IMAGE_TYPE_RW2 = 10;
    private static final int IMAGE_TYPE_SRW = 11;
    static {
        sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        for (int ifdType = 0; ifdType < EXIF_TAGS.length; ++ifdType) {
            sExifTagMapsForReading[ifdType] = new HashMap<>();
            sExifTagMapsForWriting[ifdType] = new HashMap<>();
            for (ExifTag tag : EXIF_TAGS[ifdType]) {
                sExifTagMapsForReading[ifdType].put(tag.number, tag);
                sExifTagMapsForWriting[ifdType].put(tag.name, tag);
            }
        }
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[0].number, IFD_TYPE_PREVIEW); // 330
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[1].number, IFD_TYPE_EXIF); // 34665
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[2].number, IFD_TYPE_GPS); // 34853
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[3].number, IFD_TYPE_INTEROPERABILITY); // 40965
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[4].number, IFD_TYPE_ORF_CAMERA_SETTINGS); // 8224
        sExifPointerTagMap.put(EXIF_POINTER_TAGS[5].number, IFD_TYPE_ORF_IMAGE_PROCESSING); // 8256
    }
    private final String mFilename;
    private final AssetManager.AssetInputStream mAssetInputStream;
    private int mMimeType;
    @SuppressWarnings("unchecked")
    private final HashMap<String, ExifAttribute>[] mAttributes = new HashMap[EXIF_TAGS.length];
    private Set<Integer> mAttributesOffsets = new HashSet<>(EXIF_TAGS.length);
    private ByteOrder mExifByteOrder = ByteOrder.BIG_ENDIAN;
    private boolean mHasThumbnail;
    private int mThumbnailOffset;
    private int mThumbnailLength;
    private byte[] mThumbnailBytes;
    private int mThumbnailCompression;
    private int mExifOffset;
    private int mOrfMakerNoteOffset;
    private int mOrfThumbnailOffset;
    private int mOrfThumbnailLength;
    private int mRw2JpgFromRawOffset;
    private boolean mIsSupportedFile;
    private boolean mModified;
    private static final Pattern sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
    private static final Pattern sGpsTimestampPattern =
            Pattern.compile("^([0-9][0-9]):([0-9][0-9]):([0-9][0-9])$");
    public ExifInterface(@NonNull String filename) throws IOException {
<<<<<<< /content/drive/MyDrive/merge/output/conflictFiles/platform_frameworks_support/f885b9d3675d0a2ca6bdbdd7ea1f2d72eee73948/exifinterface/src/main/java/androidx/exifinterface/media/ExifInterface.java/conflict.java
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
||||||| /content/drive/MyDrive/merge/output/conflictFiles/platform_frameworks_support/f885b9d3675d0a2ca6bdbdd7ea1f2d72eee73948/exifinterface/src/main/java/androidx/exifinterface/media/ExifInterface.java/base.java
        initForFilename(filename);
    }
    public ExifInterface(@NonNull FileDescriptor fileDescriptor) throws IOException {
        if (fileDescriptor == null) {
            throw new NullPointerException("fileDescriptor cannot be null");
        }
        mAssetInputStream = null;
        mFilename = null;
        if (Build.VERSION.SDK_INT >= 21 && isSeekableFD(fileDescriptor)) {
            mSeekableFileDescriptor = fileDescriptor;
            try {
                fileDescriptor = Os.dup(fileDescriptor);
            } catch (Exception e) {
                throw new IOException("Failed to duplicate file descriptor", e);
            }
        } else {
            mSeekableFileDescriptor = null;
=======
        if (filename == null) {
            throw new NullPointerException("filename cannot be null");
        }
        initForFilename(filename);
    }
    public ExifInterface(@NonNull FileDescriptor fileDescriptor) throws IOException {
        if (fileDescriptor == null) {
            throw new NullPointerException("fileDescriptor cannot be null");
        }
        mAssetInputStream = null;
        mFilename = null;
        if (Build.VERSION.SDK_INT >= 21 && isSeekableFD(fileDescriptor)) {
            mSeekableFileDescriptor = fileDescriptor;
            try {
                fileDescriptor = Os.dup(fileDescriptor);
            } catch (Exception e) {
                throw new IOException("Failed to duplicate file descriptor", e);
            }
        } else {
            mSeekableFileDescriptor = null;
>>>>>>> /content/drive/MyDrive/merge/output/conflictFiles/platform_frameworks_support/f885b9d3675d0a2ca6bdbdd7ea1f2d72eee73948/exifinterface/src/main/java/androidx/exifinterface/media/ExifInterface.java/theirs.java
        }
        FileInputStream in = null;
        mAssetInputStream = null;
        mFilename = filename;
        try {
            in = new FileInputStream(filename);
            loadAttributes(in);
        } finally {
            closeQuietly(in);
        }
    }
    public ExifInterface(@NonNull InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream cannot be null");
        }
        mFilename = null;
        if (inputStream instanceof AssetManager.AssetInputStream) {
            mAssetInputStream = (AssetManager.AssetInputStream) inputStream;
        } else {
            mAssetInputStream = null;
        }
        loadAttributes(inputStream);
    }
    @Nullable
    private ExifAttribute getExifAttribute(@NonNull String tag) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        if (TAG_ISO_SPEED_RATINGS.equals(tag)) {
            if (DEBUG) {
                Log.d(TAG, "getExifAttribute: Replacing TAG_ISO_SPEED_RATINGS with "
                        + "TAG_PHOTOGRAPHIC_SENSITIVITY.");
            }
            tag = TAG_PHOTOGRAPHIC_SENSITIVITY;
        }
        for (int i = 0; i < EXIF_TAGS.length; ++i) {
            ExifAttribute value = mAttributes[i].get(tag);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    @Nullable
    public String getAttribute(@NonNull String tag) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        ExifAttribute attribute = getExifAttribute(tag);
        if (attribute != null) {
            if (!sTagSetForCompatibility.contains(tag)) {
                return attribute.getStringValue(mExifByteOrder);
            }
            if (tag.equals(TAG_GPS_TIMESTAMP)) {
                if (attribute.format != IFD_FORMAT_URATIONAL
                        && attribute.format != IFD_FORMAT_SRATIONAL) {
                    Log.w(TAG, "GPS Timestamp format is not rational. format=" + attribute.format);
                    return null;
                }
                Rational[] array = (Rational[]) attribute.getValue(mExifByteOrder);
                if (array == null || array.length != 3) {
                    Log.w(TAG, "Invalid GPS Timestamp array. array=" + Arrays.toString(array));
                    return null;
                }
                return String.format("%02d:%02d:%02d",
                        (int) ((float) array[0].numerator / array[0].denominator),
                        (int) ((float) array[1].numerator / array[1].denominator),
                        (int) ((float) array[2].numerator / array[2].denominator));
            }
            try {
                return Double.toString(attribute.getDoubleValue(mExifByteOrder));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    public int getAttributeInt(@NonNull String tag, int defaultValue) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        ExifAttribute exifAttribute = getExifAttribute(tag);
        if (exifAttribute == null) {
            return defaultValue;
        }
        try {
            return exifAttribute.getIntValue(mExifByteOrder);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    public double getAttributeDouble(@NonNull String tag, double defaultValue) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        ExifAttribute exifAttribute = getExifAttribute(tag);
        if (exifAttribute == null) {
            return defaultValue;
        }
        try {
            return exifAttribute.getDoubleValue(mExifByteOrder);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    public void setAttribute(@NonNull String tag, @Nullable String value) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        if (TAG_ISO_SPEED_RATINGS.equals(tag)) {
            if (DEBUG) {
                Log.d(TAG, "setAttribute: Replacing TAG_ISO_SPEED_RATINGS with "
                        + "TAG_PHOTOGRAPHIC_SENSITIVITY.");
            }
            tag = TAG_PHOTOGRAPHIC_SENSITIVITY;
        }
        if (value != null && sTagSetForCompatibility.contains(tag)) {
            if (tag.equals(TAG_GPS_TIMESTAMP)) {
                Matcher m = sGpsTimestampPattern.matcher(value);
                if (!m.find()) {
                    Log.w(TAG, "Invalid value for " + tag + " : " + value);
                    return;
                }
                value = Integer.parseInt(m.group(1)) + "/1," + Integer.parseInt(m.group(2)) + "/1,"
                        + Integer.parseInt(m.group(3)) + "/1";
            } else {
                try {
                    double doubleValue = Double.parseDouble(value);
                    value = new Rational(doubleValue).toString();
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Invalid value for " + tag + " : " + value);
                    return;
                }
            }
        }
        for (int i = 0 ; i < EXIF_TAGS.length; ++i) {
            if (i == IFD_TYPE_THUMBNAIL && !mHasThumbnail) {
                continue;
            }
            final ExifTag exifTag = sExifTagMapsForWriting[i].get(tag);
            if (exifTag != null) {
                if (value == null) {
                    mAttributes[i].remove(tag);
                    continue;
                }
                Pair<Integer, Integer> guess = guessDataFormat(value);
                int dataFormat;
                if (exifTag.primaryFormat == guess.first || exifTag.primaryFormat == guess.second) {
                    dataFormat = exifTag.primaryFormat;
                } else if (exifTag.secondaryFormat != -1 && (exifTag.secondaryFormat == guess.first
                        || exifTag.secondaryFormat == guess.second)) {
                    dataFormat = exifTag.secondaryFormat;
                } else if (exifTag.primaryFormat == IFD_FORMAT_BYTE
                        || exifTag.primaryFormat == IFD_FORMAT_UNDEFINED
                        || exifTag.primaryFormat == IFD_FORMAT_STRING) {
                    dataFormat = exifTag.primaryFormat;
                } else {
                    Log.w(TAG, "Given tag (" + tag + ") value didn't match with one of expected "
                            + "formats: " + IFD_FORMAT_NAMES[exifTag.primaryFormat]
                            + (exifTag.secondaryFormat == -1 ? "" : ", "
                            + IFD_FORMAT_NAMES[exifTag.secondaryFormat]) + " (guess: "
                            + IFD_FORMAT_NAMES[guess.first] + (guess.second == -1 ? "" : ", "
                            + IFD_FORMAT_NAMES[guess.second]) + ")");
                    continue;
                }
                switch (dataFormat) {
                    case IFD_FORMAT_BYTE: {
                        mAttributes[i].put(tag, ExifAttribute.createByte(value));
                        break;
                    }
                    case IFD_FORMAT_UNDEFINED:
                    case IFD_FORMAT_STRING: {
                        mAttributes[i].put(tag, ExifAttribute.createString(value));
                        break;
                    }
                    case IFD_FORMAT_USHORT: {
                        final String[] values = value.split(",", -1);
                        final int[] intArray = new int[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            intArray[j] = Integer.parseInt(values[j]);
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createUShort(intArray, mExifByteOrder));
                        break;
                    }
                    case IFD_FORMAT_SLONG: {
                        final String[] values = value.split(",", -1);
                        final int[] intArray = new int[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            intArray[j] = Integer.parseInt(values[j]);
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createSLong(intArray, mExifByteOrder));
                        break;
                    }
                    case IFD_FORMAT_ULONG: {
                        final String[] values = value.split(",", -1);
                        final long[] longArray = new long[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            longArray[j] = Long.parseLong(values[j]);
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createULong(longArray, mExifByteOrder));
                        break;
                    }
                    case IFD_FORMAT_URATIONAL: {
                        final String[] values = value.split(",", -1);
                        final Rational[] rationalArray = new Rational[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            final String[] numbers = values[j].split("/", -1);
                            rationalArray[j] = new Rational((long) Double.parseDouble(numbers[0]),
                                    (long) Double.parseDouble(numbers[1]));
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createURational(rationalArray, mExifByteOrder));
                        break;
                    }
                    case IFD_FORMAT_SRATIONAL: {
                        final String[] values = value.split(",", -1);
                        final Rational[] rationalArray = new Rational[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            final String[] numbers = values[j].split("/", -1);
                            rationalArray[j] = new Rational((long) Double.parseDouble(numbers[0]),
                                    (long) Double.parseDouble(numbers[1]));
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createSRational(rationalArray, mExifByteOrder));
                        break;
                    }
                    case IFD_FORMAT_DOUBLE: {
                        final String[] values = value.split(",", -1);
                        final double[] doubleArray = new double[values.length];
                        for (int j = 0; j < values.length; ++j) {
                            doubleArray[j] = Double.parseDouble(values[j]);
                        }
                        mAttributes[i].put(tag,
                                ExifAttribute.createDouble(doubleArray, mExifByteOrder));
                        break;
                    }
                    default:
                        Log.w(TAG, "Data format isn't one of expected formats: " + dataFormat);
                        continue;
                }
            }
        }
    }
    public void resetOrientation() {
        setAttribute(TAG_ORIENTATION, Integer.toString(ORIENTATION_NORMAL));
    }
    public void rotate(int degree) {
        if (degree % 90 !=0) {
            throw new IllegalArgumentException("degree should be a multiple of 90");
        }
        int currentOrientation = getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        int currentIndex, newIndex;
        int resultOrientation;
        if (ROTATION_ORDER.contains(currentOrientation)) {
            currentIndex = ROTATION_ORDER.indexOf(currentOrientation);
            newIndex = (currentIndex + degree / 90) % 4;
            newIndex += newIndex < 0 ? 4 : 0;
            resultOrientation = ROTATION_ORDER.get(newIndex);
        } else if (FLIPPED_ROTATION_ORDER.contains(currentOrientation)) {
            currentIndex = FLIPPED_ROTATION_ORDER.indexOf(currentOrientation);
            newIndex = (currentIndex + degree / 90) % 4;
            newIndex += newIndex < 0 ? 4 : 0;
            resultOrientation = FLIPPED_ROTATION_ORDER.get(newIndex);
        } else {
            resultOrientation = ORIENTATION_UNDEFINED;
        }
        setAttribute(TAG_ORIENTATION, Integer.toString(resultOrientation));
    }
    public void flipVertically() {
        int currentOrientation = getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        int resultOrientation;
        switch (currentOrientation) {
            case ORIENTATION_FLIP_HORIZONTAL:
                resultOrientation = ORIENTATION_ROTATE_180;
                break;
            case ORIENTATION_ROTATE_180:
                resultOrientation = ORIENTATION_FLIP_HORIZONTAL;
                break;
            case ORIENTATION_FLIP_VERTICAL:
                resultOrientation = ORIENTATION_NORMAL;
                break;
            case ORIENTATION_TRANSPOSE:
                resultOrientation = ORIENTATION_ROTATE_270;
                break;
            case ORIENTATION_ROTATE_90:
                resultOrientation = ORIENTATION_TRANSVERSE;
                break;
            case ORIENTATION_TRANSVERSE:
                resultOrientation = ORIENTATION_ROTATE_90;
                break;
            case ORIENTATION_ROTATE_270:
                resultOrientation = ORIENTATION_TRANSPOSE;
                break;
            case ORIENTATION_NORMAL:
                resultOrientation = ORIENTATION_FLIP_VERTICAL;
                break;
            case ORIENTATION_UNDEFINED:
            default:
                resultOrientation = ORIENTATION_UNDEFINED;
                break;
        }
        setAttribute(TAG_ORIENTATION, Integer.toString(resultOrientation));
    }
    public void flipHorizontally() {
        int currentOrientation = getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        int resultOrientation;
        switch (currentOrientation) {
            case ORIENTATION_FLIP_HORIZONTAL:
                resultOrientation = ORIENTATION_NORMAL;
                break;
            case ORIENTATION_ROTATE_180:
                resultOrientation = ORIENTATION_FLIP_VERTICAL;
                break;
            case ORIENTATION_FLIP_VERTICAL:
                resultOrientation = ORIENTATION_ROTATE_180;
                break;
            case ORIENTATION_TRANSPOSE:
                resultOrientation = ORIENTATION_ROTATE_90;
                break;
            case ORIENTATION_ROTATE_90:
                resultOrientation = ORIENTATION_TRANSPOSE;
                break;
            case ORIENTATION_TRANSVERSE:
                resultOrientation = ORIENTATION_ROTATE_270;
                break;
            case ORIENTATION_ROTATE_270:
                resultOrientation = ORIENTATION_TRANSVERSE;
                break;
            case ORIENTATION_NORMAL:
                resultOrientation = ORIENTATION_FLIP_HORIZONTAL;
                break;
            case ORIENTATION_UNDEFINED:
            default:
                resultOrientation = ORIENTATION_UNDEFINED;
                break;
        }
        setAttribute(TAG_ORIENTATION, Integer.toString(resultOrientation));
    }
    public boolean isFlipped() {
        int orientation = getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        switch (orientation) {
            case ORIENTATION_FLIP_HORIZONTAL:
            case ORIENTATION_TRANSVERSE:
            case ORIENTATION_FLIP_VERTICAL:
            case ORIENTATION_TRANSPOSE:
                return true;
            default:
                return false;
        }
    }
    public int getRotationDegrees() {
        int orientation = getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
            case ORIENTATION_TRANSVERSE:
                return 90;
            case ORIENTATION_ROTATE_180:
            case ORIENTATION_FLIP_VERTICAL:
                return 180;
            case ORIENTATION_ROTATE_270:
            case ORIENTATION_TRANSPOSE:
                return 270;
            case ORIENTATION_UNDEFINED:
            case ORIENTATION_NORMAL:
            case ORIENTATION_FLIP_HORIZONTAL:
            default:
                return 0;
        }
    }
    private boolean updateAttribute(String tag, ExifAttribute value) {
        boolean updated = false;
        for (int i = 0 ; i < EXIF_TAGS.length; ++i) {
            if (mAttributes[i].containsKey(tag)) {
                mAttributes[i].put(tag, value);
                updated = true;
            }
        }
        return updated;
    }
    private void removeAttribute(String tag) {
        for (int i = 0 ; i < EXIF_TAGS.length; ++i) {
            mAttributes[i].remove(tag);
        }
    }
    private void loadAttributes(@NonNull InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("inputstream shouldn't be null");
        }
        try {
            for (int i = 0; i < EXIF_TAGS.length; ++i) {
                mAttributes[i] = new HashMap<>();
            }
            in = new BufferedInputStream(in, SIGNATURE_CHECK_SIZE);
            mMimeType = getMimeType((BufferedInputStream) in);
            ByteOrderedDataInputStream inputStream = new ByteOrderedDataInputStream(in);
            switch (mMimeType) {
                case IMAGE_TYPE_JPEG: {
                    getJpegAttributes(inputStream, 0, IFD_TYPE_PRIMARY); // 0 is offset
                    break;
                }
                case IMAGE_TYPE_RAF: {
                    getRafAttributes(inputStream);
                    break;
                }
                case IMAGE_TYPE_ORF: {
                    getOrfAttributes(inputStream);
                    break;
                }
                case IMAGE_TYPE_RW2: {
                    getRw2Attributes(inputStream);
                    break;
                }
                case IMAGE_TYPE_ARW:
                case IMAGE_TYPE_CR2:
                case IMAGE_TYPE_DNG:
                case IMAGE_TYPE_NEF:
                case IMAGE_TYPE_NRW:
                case IMAGE_TYPE_PEF:
                case IMAGE_TYPE_SRW:
                case IMAGE_TYPE_UNKNOWN: {
                    getRawAttributes(inputStream);
                    break;
                }
                default: {
                    break;
                }
            }
            setThumbnailData(inputStream);
            mIsSupportedFile = true;
        } catch (IOException e) {
            mIsSupportedFile = false;
            if (DEBUG) {
                Log.w(TAG, "Invalid image: ExifInterface got an unsupported image format file"
                        + "(ExifInterface supports JPEG and some RAW image formats only) "
                        + "or a corrupted JPEG file to ExifInterface.", e);
            }
        } finally {
            addDefaultValuesForCompatibility();
            if (DEBUG) {
                printAttributes();
            }
        }
    }
    private void printAttributes() {
        for (int i = 0; i < mAttributes.length; ++i) {
            Log.d(TAG, "The size of tag group[" + i + "]: " + mAttributes[i].size());
            for (Map.Entry<String, ExifAttribute> entry : mAttributes[i].entrySet()) {
                final ExifAttribute tagValue = entry.getValue();
                Log.d(TAG, "tagName: " + entry.getKey() + ", tagType: " + tagValue.toString()
                        + ", tagValue: '" + tagValue.getStringValue(mExifByteOrder) + "'");
            }
        }
    }
    public void saveAttributes() throws IOException {
        if (!mIsSupportedFile || mMimeType != IMAGE_TYPE_JPEG) {
            throw new IOException("ExifInterface only supports saving attributes on JPEG formats.");
        }
        if (mFilename == null) {
            throw new IOException(
                    "ExifInterface does not support saving attributes for the current input.");
        }
        mModified = true;
        mThumbnailBytes = getThumbnail();
        File tempFile = new File(mFilename + ".tmp");
        File originalFile = new File(mFilename);
        if (!originalFile.renameTo(tempFile)) {
            throw new IOException("Could not rename to " + tempFile.getAbsolutePath());
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(tempFile);
            out = new FileOutputStream(mFilename);
            saveJpegAttributes(in, out);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
            tempFile.delete();
        }
        mThumbnailBytes = null;
    }
    public boolean hasThumbnail() {
        return mHasThumbnail;
    }
    public boolean hasAttribute(@NonNull String tag) {
        return getExifAttribute(tag) != null;
    }
    @Nullable
    public byte[] getThumbnail() {
        if (mThumbnailCompression == DATA_JPEG || mThumbnailCompression == DATA_JPEG_COMPRESSED) {
            return getThumbnailBytes();
        }
        return null;
    }
    @Nullable
    public byte[] getThumbnailBytes() {
        if (!mHasThumbnail) {
            return null;
        }
        if (mThumbnailBytes != null) {
            return mThumbnailBytes;
        }
        InputStream in = null;
        try {
            if (mAssetInputStream != null) {
                in = mAssetInputStream;
                if (in.markSupported()) {
                    in.reset();
                } else {
                    Log.d(TAG, "Cannot read thumbnail from inputstream without mark/reset support");
                    return null;
                }
            } else if (mFilename != null) {
                in = new FileInputStream(mFilename);
            }
            if (in == null) {
                throw new FileNotFoundException();
            }
            if (in.skip(mThumbnailOffset) != mThumbnailOffset) {
                throw new IOException("Corrupted image");
            }
            byte[] buffer = new byte[mThumbnailLength];
            if (in.read(buffer) != mThumbnailLength) {
                throw new IOException("Corrupted image");
            }
            mThumbnailBytes = buffer;
            return buffer;
        } catch (IOException e) {
            Log.d(TAG, "Encountered exception while getting thumbnail", e);
        } finally {
            closeQuietly(in);
        }
        return null;
    }
    @Nullable
    public Bitmap getThumbnailBitmap() {
        if (!mHasThumbnail) {
            return null;
        } else if (mThumbnailBytes == null) {
            mThumbnailBytes = getThumbnailBytes();
        }
        if (mThumbnailCompression == DATA_JPEG || mThumbnailCompression == DATA_JPEG_COMPRESSED) {
            return BitmapFactory.decodeByteArray(mThumbnailBytes, 0, mThumbnailLength);
        } else if (mThumbnailCompression == DATA_UNCOMPRESSED) {
            int[] rgbValues = new int[mThumbnailBytes.length / 3];
            byte alpha = (byte) 0xff000000;
            for (int i = 0; i < rgbValues.length; i++) {
                rgbValues[i] = alpha + (mThumbnailBytes[3 * i] << 16)
                        + (mThumbnailBytes[3 * i + 1] << 8) + mThumbnailBytes[3 * i + 2];
            }
            ExifAttribute imageLengthAttribute =
                    (ExifAttribute) mAttributes[IFD_TYPE_THUMBNAIL].get(TAG_IMAGE_LENGTH);
            ExifAttribute imageWidthAttribute =
                    (ExifAttribute) mAttributes[IFD_TYPE_THUMBNAIL].get(TAG_IMAGE_WIDTH);
            if (imageLengthAttribute != null && imageWidthAttribute != null) {
                int imageLength = imageLengthAttribute.getIntValue(mExifByteOrder);
                int imageWidth = imageWidthAttribute.getIntValue(mExifByteOrder);
                return Bitmap.createBitmap(
                        rgbValues, imageWidth, imageLength, Bitmap.Config.ARGB_8888);
            }
        }
        return null;
    }
    public boolean isThumbnailCompressed() {
        if (!mHasThumbnail) {
            return false;
        }
        if (mThumbnailCompression == DATA_JPEG || mThumbnailCompression == DATA_JPEG_COMPRESSED) {
            return true;
        }
        return false;
    }
    @Nullable
    public long[] getThumbnailRange() {
        if (mModified) {
            throw new IllegalStateException(
                    "The underlying file has been modified since being parsed");
        }
        if (mHasThumbnail) {
            return new long[] { mThumbnailOffset, mThumbnailLength };
        } else {
            return null;
        }
    }
    @Nullable
    public long[] getAttributeRange(@NonNull String tag) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        if (mModified) {
            throw new IllegalStateException(
                    "The underlying file has been modified since being parsed");
        }
        final ExifAttribute attribute = getExifAttribute(tag);
        if (attribute != null) {
            return new long[] { attribute.bytesOffset, attribute.bytes.length };
        } else {
            return null;
        }
    }
    @Nullable
    public byte[] getAttributeBytes(@NonNull String tag) {
        if (tag == null) {
            throw new NullPointerException("tag shouldn't be null");
        }
        final ExifAttribute attribute = getExifAttribute(tag);
        if (attribute != null) {
            return attribute.bytes;
        } else {
            return null;
        }
    }
    @Deprecated
    public boolean getLatLong(float output[]) {
        double[] latLong = getLatLong();
        if (latLong == null) {
            return false;
        }
        output[0] = (float) latLong[0];
        output[1] = (float) latLong[1];
        return true;
    }
    @Nullable
    public double[] getLatLong() {
        String latValue = getAttribute(TAG_GPS_LATITUDE);
        String latRef = getAttribute(TAG_GPS_LATITUDE_REF);
        String lngValue = getAttribute(TAG_GPS_LONGITUDE);
        String lngRef = getAttribute(TAG_GPS_LONGITUDE_REF);
        if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
            try {
                double latitude = convertRationalLatLonToDouble(latValue, latRef);
                double longitude = convertRationalLatLonToDouble(lngValue, lngRef);
                return new double[] {latitude, longitude};
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Latitude/longitude values are not parseable. " +
                        String.format("latValue=%s, latRef=%s, lngValue=%s, lngRef=%s",
                                latValue, latRef, lngValue, lngRef));
            }
        }
        return null;
    }
    public void setGpsInfo(Location location) {
        if (location == null) {
            return;
        }
        setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, location.getProvider());
        setLatLong(location.getLatitude(), location.getLongitude());
        setAltitude(location.getAltitude());
        setAttribute(TAG_GPS_SPEED_REF, "K");
        setAttribute(TAG_GPS_SPEED, new Rational(location.getSpeed()
        String[] dateTime = sFormatter.format(new Date(location.getTime())).split("\\s+", -1);
        setAttribute(ExifInterface.TAG_GPS_DATESTAMP, dateTime[0]);
        setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, dateTime[1]);
    }
    public void setLatLong(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0 || Double.isNaN(latitude)) {
            throw new IllegalArgumentException("Latitude value " + latitude + " is not valid.");
        }
        if (longitude < -180.0 || longitude > 180.0 || Double.isNaN(longitude)) {
            throw new IllegalArgumentException("Longitude value " + longitude + " is not valid.");
        }
        setAttribute(TAG_GPS_LATITUDE_REF, latitude >= 0 ? "N" : "S");
        setAttribute(TAG_GPS_LATITUDE, convertDecimalDegree(Math.abs(latitude)));
        setAttribute(TAG_GPS_LONGITUDE_REF, longitude >= 0 ? "E" : "W");
        setAttribute(TAG_GPS_LONGITUDE, convertDecimalDegree(Math.abs(longitude)));
    }
    public double getAltitude(double defaultValue) {
        double altitude = getAttributeDouble(TAG_GPS_ALTITUDE, -1);
        int ref = getAttributeInt(TAG_GPS_ALTITUDE_REF, -1);
        if (altitude >= 0 && ref >= 0) {
            return (altitude * ((ref == 1) ? -1 : 1));
        } else {
            return defaultValue;
        }
    }
    public void setAltitude(double altitude) {
        String ref = altitude >= 0 ? "0" : "1";
        setAttribute(TAG_GPS_ALTITUDE, new Rational(Math.abs(altitude)).toString());
        setAttribute(TAG_GPS_ALTITUDE_REF, ref);
    }
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setDateTime(long timeStamp) {
        long sub = timeStamp % 1000;
        setAttribute(TAG_DATETIME, sFormatter.format(new Date(timeStamp)));
        setAttribute(TAG_SUBSEC_TIME, Long.toString(sub));
    }
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public long getDateTime() {
        return parseDateTime(getAttribute(TAG_DATETIME),
                getAttribute(TAG_SUBSEC_TIME));
    }
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public long getDateTimeDigitized() {
        return parseDateTime(getAttribute(TAG_DATETIME_DIGITIZED),
                getAttribute(TAG_SUBSEC_TIME_DIGITIZED));
    }
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public long getDateTimeOriginal() {
        return parseDateTime(getAttribute(TAG_DATETIME_ORIGINAL),
                getAttribute(TAG_SUBSEC_TIME_ORIGINAL));
    }
    private static long parseDateTime(@Nullable String dateTimeString, @Nullable String subSecs) {
        if (dateTimeString == null
                || !sNonZeroTimePattern.matcher(dateTimeString).matches()) return -1;
        ParsePosition pos = new ParsePosition(0);
        try {
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) return -1;
            long msecs = datetime.getTime();
            if (subSecs != null) {
                try {
                    long sub = Long.parseLong(subSecs);
                    while (sub > 1000) {
                        sub /= 10;
                    }
                    msecs += sub;
                } catch (NumberFormatException e) {
                }
            }
            return msecs;
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public long getGpsDateTime() {
        String date = getAttribute(TAG_GPS_DATESTAMP);
        String time = getAttribute(TAG_GPS_TIMESTAMP);
        if (date == null || time == null
                || (!sNonZeroTimePattern.matcher(date).matches()
                && !sNonZeroTimePattern.matcher(time).matches())) {
            return -1;
        }
        String dateTimeString = date + ' ' + time;
        ParsePosition pos = new ParsePosition(0);
        try {
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) return -1;
            return datetime.getTime();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }
    private static double convertRationalLatLonToDouble(String rationalString, String ref) {
        try {
            String [] parts = rationalString.split(",", -1);
            String [] pair;
            pair = parts[0].split("/", -1);
            double degrees = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            pair = parts[1].split("/", -1);
            double minutes = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            pair = parts[2].split("/", -1);
            double seconds = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ((ref.equals("S") || ref.equals("W"))) {
                return -result;
            } else if (ref.equals("N") || ref.equals("E")) {
                return result;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }
    private String convertDecimalDegree(double decimalDegree) {
        long degrees = (long) decimalDegree;
        long minutes = (long) ((decimalDegree - degrees) * 60.0);
        long seconds = Math.round((decimalDegree - degrees - minutes / 60.0) * 3600.0 * 1e7);
        return degrees + "/1," + minutes + "/1," + seconds + "/10000000";
    }
    private int getMimeType(BufferedInputStream in) throws IOException {
        in.mark(SIGNATURE_CHECK_SIZE);
        byte[] signatureCheckBytes = new byte[SIGNATURE_CHECK_SIZE];
        in.read(signatureCheckBytes);
        in.reset();
        if (isJpegFormat(signatureCheckBytes)) {
            return IMAGE_TYPE_JPEG;
        } else if (isRafFormat(signatureCheckBytes)) {
            return IMAGE_TYPE_RAF;
        } else if (isOrfFormat(signatureCheckBytes)) {
            return IMAGE_TYPE_ORF;
        } else if (isRw2Format(signatureCheckBytes)) {
            return IMAGE_TYPE_RW2;
        }
        return IMAGE_TYPE_UNKNOWN;
    }
    private static boolean isJpegFormat(byte[] signatureCheckBytes) throws IOException {
        for (int i = 0; i < JPEG_SIGNATURE.length; i++) {
            if (signatureCheckBytes[i] != JPEG_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }
    private boolean isRafFormat(byte[] signatureCheckBytes) throws IOException {
        byte[] rafSignatureBytes = RAF_SIGNATURE.getBytes(Charset.defaultCharset());
        for (int i = 0; i < rafSignatureBytes.length; i++) {
            if (signatureCheckBytes[i] != rafSignatureBytes[i]) {
                return false;
            }
        }
        return true;
    }
    private boolean isOrfFormat(byte[] signatureCheckBytes) throws IOException {
        ByteOrderedDataInputStream signatureInputStream =
                new ByteOrderedDataInputStream(signatureCheckBytes);
        mExifByteOrder = readByteOrder(signatureInputStream);
        signatureInputStream.setByteOrder(mExifByteOrder);
        short orfSignature = signatureInputStream.readShort();
        signatureInputStream.close();
        return orfSignature == ORF_SIGNATURE_1 || orfSignature == ORF_SIGNATURE_2;
    }
    private boolean isRw2Format(byte[] signatureCheckBytes) throws IOException {
        ByteOrderedDataInputStream signatureInputStream =
                new ByteOrderedDataInputStream(signatureCheckBytes);
        mExifByteOrder = readByteOrder(signatureInputStream);
        signatureInputStream.setByteOrder(mExifByteOrder);
        short signatureByte = signatureInputStream.readShort();
        signatureInputStream.close();
        return signatureByte == RW2_SIGNATURE;
    }
    private void getJpegAttributes(ByteOrderedDataInputStream in, int jpegOffset, int imageType)
            throws IOException {
        if (DEBUG) {
            Log.d(TAG, "getJpegAttributes starting with: " + in);
        }
        in.setByteOrder(ByteOrder.BIG_ENDIAN);
        in.seek(jpegOffset);
        int bytesRead = jpegOffset;
        byte marker;
        if ((marker = in.readByte()) != MARKER) {
            throw new IOException("Invalid marker: " + Integer.toHexString(marker & 0xff));
        }
        ++bytesRead;
        if (in.readByte() != MARKER_SOI) {
            throw new IOException("Invalid marker: " + Integer.toHexString(marker & 0xff));
        }
        ++bytesRead;
        while (true) {
            marker = in.readByte();
            if (marker != MARKER) {
                throw new IOException("Invalid marker:" + Integer.toHexString(marker & 0xff));
            }
            ++bytesRead;
            marker = in.readByte();
            if (DEBUG) {
                Log.d(TAG, "Found JPEG segment indicator: " + Integer.toHexString(marker & 0xff));
            }
            ++bytesRead;
            if (marker == MARKER_EOI || marker == MARKER_SOS) {
                break;
            }
            int length = in.readUnsignedShort() - 2;
            bytesRead += 2;
            if (DEBUG) {
                Log.d(TAG, "JPEG segment: " + Integer.toHexString(marker & 0xff) + " (length: "
                        + (length + 2) + ")");
            }
            if (length < 0) {
                throw new IOException("Invalid length");
            }
            switch (marker) {
                case MARKER_APP1: {
                    final int start = bytesRead;
                    final byte[] bytes = new byte[length];
                    in.readFully(bytes);
                    bytesRead += length;
                    length = 0;
                    if (startsWith(bytes, IDENTIFIER_EXIF_APP1)) {
                        final long offset = start + IDENTIFIER_EXIF_APP1.length;
                        final byte[] value = Arrays.copyOfRange(bytes, IDENTIFIER_EXIF_APP1.length,
                                bytes.length);
                        readExifSegment(value, imageType);
                        mExifOffset = (int) offset;
                    } else if (startsWith(bytes, IDENTIFIER_XMP_APP1)) {
                        final long offset = start + IDENTIFIER_XMP_APP1.length;
                        final byte[] value = Arrays.copyOfRange(bytes,
                                IDENTIFIER_XMP_APP1.length, bytes.length);
                        if (getAttribute(TAG_XMP) == null) {
                            mAttributes[IFD_TYPE_PRIMARY].put(TAG_XMP, new ExifAttribute(
                                    IFD_FORMAT_BYTE, value.length, offset, value));
                        }
                    }
                }
                case MARKER_COM: {
                    byte[] bytes = new byte[length];
                    if (in.read(bytes) != length) {
                        throw new IOException("Invalid exif");
                    }
                    length = 0;
                    if (getAttribute(TAG_USER_COMMENT) == null) {
                        mAttributes[IFD_TYPE_EXIF].put(TAG_USER_COMMENT, ExifAttribute.createString(
                                new String(bytes, ASCII)));
                    }
                    break;
                }
                case MARKER_SOF0:
                case MARKER_SOF1:
                case MARKER_SOF2:
                case MARKER_SOF3:
                case MARKER_SOF5:
                case MARKER_SOF6:
                case MARKER_SOF7:
                case MARKER_SOF9:
                case MARKER_SOF10:
                case MARKER_SOF11:
                case MARKER_SOF13:
                case MARKER_SOF14:
                case MARKER_SOF15: {
                    if (in.skipBytes(1) != 1) {
                        throw new IOException("Invalid SOFx");
                    }
                    mAttributes[imageType].put(TAG_IMAGE_LENGTH, ExifAttribute.createULong(
                            in.readUnsignedShort(), mExifByteOrder));
                    mAttributes[imageType].put(TAG_IMAGE_WIDTH, ExifAttribute.createULong(
                            in.readUnsignedShort(), mExifByteOrder));
                    length -= 5;
                    break;
                }
                default: {
                    break;
                }
            }
            if (length < 0) {
                throw new IOException("Invalid length");
            }
            if (in.skipBytes(length) != length) {
                throw new IOException("Invalid JPEG segment");
            }
            bytesRead += length;
        }
        in.setByteOrder(mExifByteOrder);
    }
    private void getRawAttributes(ByteOrderedDataInputStream in) throws IOException {
        parseTiffHeaders(in, in.available());
        readImageFileDirectory(in, IFD_TYPE_PRIMARY);
        updateImageSizeValues(in, IFD_TYPE_PRIMARY);
        updateImageSizeValues(in, IFD_TYPE_PREVIEW);
        updateImageSizeValues(in, IFD_TYPE_THUMBNAIL);
        validateImages(in);
        if (mMimeType == IMAGE_TYPE_PEF) {
            ExifAttribute makerNoteAttribute =
                    (ExifAttribute) mAttributes[IFD_TYPE_EXIF].get(TAG_MAKER_NOTE);
            if (makerNoteAttribute != null) {
                ByteOrderedDataInputStream makerNoteDataInputStream =
                        new ByteOrderedDataInputStream(makerNoteAttribute.bytes);
                makerNoteDataInputStream.setByteOrder(mExifByteOrder);
                makerNoteDataInputStream.seek(PEF_MAKER_NOTE_SKIP_SIZE);
                readImageFileDirectory(makerNoteDataInputStream, IFD_TYPE_PEF);
                ExifAttribute colorSpaceAttribute =
                        (ExifAttribute) mAttributes[IFD_TYPE_PEF].get(TAG_COLOR_SPACE);
                if (colorSpaceAttribute != null) {
                    mAttributes[IFD_TYPE_EXIF].put(TAG_COLOR_SPACE, colorSpaceAttribute);
                }
            }
        }
    }
    private void getRafAttributes(ByteOrderedDataInputStream in) throws IOException {
        in.skipBytes(RAF_OFFSET_TO_JPEG_IMAGE_OFFSET);
        byte[] jpegOffsetBytes = new byte[4];
        byte[] cfaHeaderOffsetBytes = new byte[4];
        in.read(jpegOffsetBytes);
        in.skipBytes(RAF_JPEG_LENGTH_VALUE_SIZE);
        in.read(cfaHeaderOffsetBytes);
        int rafJpegOffset = ByteBuffer.wrap(jpegOffsetBytes).getInt();
        int rafCfaHeaderOffset = ByteBuffer.wrap(cfaHeaderOffsetBytes).getInt();
        getJpegAttributes(in, rafJpegOffset, IFD_TYPE_PREVIEW);
        in.seek(rafCfaHeaderOffset);
        in.setByteOrder(ByteOrder.BIG_ENDIAN);
        int numberOfDirectoryEntry = in.readInt();
        if (DEBUG) {
            Log.d(TAG, "numberOfDirectoryEntry: " + numberOfDirectoryEntry);
        }
        for (int i = 0; i < numberOfDirectoryEntry; ++i) {
            int tagNumber = in.readUnsignedShort();
            int numberOfBytes = in.readUnsignedShort();
            if (tagNumber == TAG_RAF_IMAGE_SIZE.number) {
                int imageLength = in.readShort();
                int imageWidth = in.readShort();
                ExifAttribute imageLengthAttribute =
                        ExifAttribute.createUShort(imageLength, mExifByteOrder);
                ExifAttribute imageWidthAttribute =
                        ExifAttribute.createUShort(imageWidth, mExifByteOrder);
                mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_LENGTH, imageLengthAttribute);
                mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_WIDTH, imageWidthAttribute);
                if (DEBUG) {
                    Log.d(TAG, "Updated to length: " + imageLength + ", width: " + imageWidth);
                }
                return;
            }
            in.skipBytes(numberOfBytes);
        }
    }
    private void getOrfAttributes(ByteOrderedDataInputStream in) throws IOException {
        getRawAttributes(in);
        ExifAttribute makerNoteAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_EXIF].get(TAG_MAKER_NOTE);
        if (makerNoteAttribute != null) {
            ByteOrderedDataInputStream makerNoteDataInputStream =
                    new ByteOrderedDataInputStream(makerNoteAttribute.bytes);
            makerNoteDataInputStream.setByteOrder(mExifByteOrder);
            byte[] makerNoteHeader1Bytes = new byte[ORF_MAKER_NOTE_HEADER_1.length];
            makerNoteDataInputStream.readFully(makerNoteHeader1Bytes);
            makerNoteDataInputStream.seek(0);
            byte[] makerNoteHeader2Bytes = new byte[ORF_MAKER_NOTE_HEADER_2.length];
            makerNoteDataInputStream.readFully(makerNoteHeader2Bytes);
            if (Arrays.equals(makerNoteHeader1Bytes, ORF_MAKER_NOTE_HEADER_1)) {
                makerNoteDataInputStream.seek(ORF_MAKER_NOTE_HEADER_1_SIZE);
            } else if (Arrays.equals(makerNoteHeader2Bytes, ORF_MAKER_NOTE_HEADER_2)) {
                makerNoteDataInputStream.seek(ORF_MAKER_NOTE_HEADER_2_SIZE);
            }
            readImageFileDirectory(makerNoteDataInputStream, IFD_TYPE_ORF_MAKER_NOTE);
            ExifAttribute imageStartAttribute = (ExifAttribute)
                    mAttributes[IFD_TYPE_ORF_CAMERA_SETTINGS].get(TAG_ORF_PREVIEW_IMAGE_START);
            ExifAttribute imageLengthAttribute = (ExifAttribute)
                    mAttributes[IFD_TYPE_ORF_CAMERA_SETTINGS].get(TAG_ORF_PREVIEW_IMAGE_LENGTH);
            if (imageStartAttribute != null && imageLengthAttribute != null) {
                mAttributes[IFD_TYPE_PREVIEW].put(TAG_JPEG_INTERCHANGE_FORMAT,
                        imageStartAttribute);
                mAttributes[IFD_TYPE_PREVIEW].put(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
                        imageLengthAttribute);
            }
            ExifAttribute aspectFrameAttribute = (ExifAttribute)
                    mAttributes[IFD_TYPE_ORF_IMAGE_PROCESSING].get(TAG_ORF_ASPECT_FRAME);
            if (aspectFrameAttribute != null) {
                int[] aspectFrameValues = (int[]) aspectFrameAttribute.getValue(mExifByteOrder);
                if (aspectFrameValues == null || aspectFrameValues.length != 4) {
                    Log.w(TAG, "Invalid aspect frame values. frame="
                            + Arrays.toString(aspectFrameValues));
                    return;
                }
                if (aspectFrameValues[2] > aspectFrameValues[0] &&
                        aspectFrameValues[3] > aspectFrameValues[1]) {
                    int primaryImageWidth = aspectFrameValues[2] - aspectFrameValues[0] + 1;
                    int primaryImageLength = aspectFrameValues[3] - aspectFrameValues[1] + 1;
                    if (primaryImageWidth < primaryImageLength) {
                        primaryImageWidth += primaryImageLength;
                        primaryImageLength = primaryImageWidth - primaryImageLength;
                        primaryImageWidth -= primaryImageLength;
                    }
                    ExifAttribute primaryImageWidthAttribute =
                            ExifAttribute.createUShort(primaryImageWidth, mExifByteOrder);
                    ExifAttribute primaryImageLengthAttribute =
                            ExifAttribute.createUShort(primaryImageLength, mExifByteOrder);
                    mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_WIDTH, primaryImageWidthAttribute);
                    mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_LENGTH, primaryImageLengthAttribute);
                }
            }
        }
    }
    private void getRw2Attributes(ByteOrderedDataInputStream in) throws IOException {
        getRawAttributes(in);
        ExifAttribute jpgFromRawAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_PRIMARY].get(TAG_RW2_JPG_FROM_RAW);
        if (jpgFromRawAttribute != null) {
            getJpegAttributes(in, mRw2JpgFromRawOffset, IFD_TYPE_PREVIEW);
        }
        ExifAttribute rw2IsoAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_PRIMARY].get(TAG_RW2_ISO);
        ExifAttribute exifIsoAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_EXIF].get(TAG_PHOTOGRAPHIC_SENSITIVITY);
        if (rw2IsoAttribute != null && exifIsoAttribute == null) {
            mAttributes[IFD_TYPE_EXIF].put(TAG_PHOTOGRAPHIC_SENSITIVITY, rw2IsoAttribute);
        }
    }
    private void saveJpegAttributes(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        if (DEBUG) {
            Log.d(TAG, "saveJpegAttributes starting with (inputStream: " + inputStream
                    + ", outputStream: " + outputStream + ")");
        }
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        ByteOrderedDataOutputStream dataOutputStream =
                new ByteOrderedDataOutputStream(outputStream, ByteOrder.BIG_ENDIAN);
        if (dataInputStream.readByte() != MARKER) {
            throw new IOException("Invalid marker");
        }
        dataOutputStream.writeByte(MARKER);
        if (dataInputStream.readByte() != MARKER_SOI) {
            throw new IOException("Invalid marker");
        }
        dataOutputStream.writeByte(MARKER_SOI);
        dataOutputStream.writeByte(MARKER);
        dataOutputStream.writeByte(MARKER_APP1);
        writeExifSegment(dataOutputStream, 6);
        byte[] bytes = new byte[4096];
        while (true) {
            byte marker = dataInputStream.readByte();
            if (marker != MARKER) {
                throw new IOException("Invalid marker");
            }
            marker = dataInputStream.readByte();
            switch (marker) {
                case MARKER_APP1: {
                    int length = dataInputStream.readUnsignedShort() - 2;
                    if (length < 0) {
                        throw new IOException("Invalid length");
                    }
                    byte[] identifier = new byte[6];
                    if (length >= 6) {
                        if (dataInputStream.read(identifier) != 6) {
                            throw new IOException("Invalid exif");
                        }
                        if (Arrays.equals(identifier, IDENTIFIER_EXIF_APP1)) {
                            if (dataInputStream.skipBytes(length - 6) != length - 6) {
                                throw new IOException("Invalid length");
                            }
                            break;
                        }
                    }
                    dataOutputStream.writeByte(MARKER);
                    dataOutputStream.writeByte(marker);
                    dataOutputStream.writeUnsignedShort(length + 2);
                    if (length >= 6) {
                        length -= 6;
                        dataOutputStream.write(identifier);
                    }
                    int read;
                    while (length > 0 && (read = dataInputStream.read(
                            bytes, 0, Math.min(length, bytes.length))) >= 0) {
                        dataOutputStream.write(bytes, 0, read);
                        length -= read;
                    }
                    break;
                }
                case MARKER_EOI:
                case MARKER_SOS: {
                    dataOutputStream.writeByte(MARKER);
                    dataOutputStream.writeByte(marker);
                    copy(dataInputStream, dataOutputStream);
                    return;
                }
                default: {
                    dataOutputStream.writeByte(MARKER);
                    dataOutputStream.writeByte(marker);
                    int length = dataInputStream.readUnsignedShort();
                    dataOutputStream.writeUnsignedShort(length);
                    length -= 2;
                    if (length < 0) {
                        throw new IOException("Invalid length");
                    }
                    int read;
                    while (length > 0 && (read = dataInputStream.read(
                            bytes, 0, Math.min(length, bytes.length))) >= 0) {
                        dataOutputStream.write(bytes, 0, read);
                        length -= read;
                    }
                    break;
                }
            }
        }
    }
    private void readExifSegment(byte[] exifBytes, int imageType) throws IOException {
        ByteOrderedDataInputStream dataInputStream =
                new ByteOrderedDataInputStream(exifBytes);
        parseTiffHeaders(dataInputStream, exifBytes.length);
        readImageFileDirectory(dataInputStream, imageType);
    }
    private void addDefaultValuesForCompatibility() {
        String valueOfDateTimeOriginal = getAttribute(TAG_DATETIME_ORIGINAL);
        if (valueOfDateTimeOriginal != null && getAttribute(TAG_DATETIME) == null) {
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_DATETIME,
                    ExifAttribute.createString(valueOfDateTimeOriginal));
        }
        if (getAttribute(TAG_IMAGE_WIDTH) == null) {
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_WIDTH,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (getAttribute(TAG_IMAGE_LENGTH) == null) {
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_LENGTH,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (getAttribute(TAG_ORIENTATION) == null) {
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_ORIENTATION,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (getAttribute(TAG_LIGHT_SOURCE) == null) {
            mAttributes[IFD_TYPE_EXIF].put(TAG_LIGHT_SOURCE,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
    }
    private ByteOrder readByteOrder(ByteOrderedDataInputStream dataInputStream)
            throws IOException {
        short byteOrder = dataInputStream.readShort();
        switch (byteOrder) {
            case BYTE_ALIGN_II:
                if (DEBUG) {
                    Log.d(TAG, "readExifSegment: Byte Align II");
                }
                return ByteOrder.LITTLE_ENDIAN;
            case BYTE_ALIGN_MM:
                if (DEBUG) {
                    Log.d(TAG, "readExifSegment: Byte Align MM");
                }
                return ByteOrder.BIG_ENDIAN;
            default:
                throw new IOException("Invalid byte order: " + Integer.toHexString(byteOrder));
        }
    }
    private void parseTiffHeaders(ByteOrderedDataInputStream dataInputStream,
            int exifBytesLength) throws IOException {
        mExifByteOrder = readByteOrder(dataInputStream);
        dataInputStream.setByteOrder(mExifByteOrder);
        int startCode = dataInputStream.readUnsignedShort();
        if (mMimeType != IMAGE_TYPE_ORF && mMimeType != IMAGE_TYPE_RW2 && startCode != START_CODE) {
            throw new IOException("Invalid start code: " + Integer.toHexString(startCode));
        }
        int firstIfdOffset = dataInputStream.readInt();
        if (firstIfdOffset < 8 || firstIfdOffset >= exifBytesLength) {
            throw new IOException("Invalid first Ifd offset: " + firstIfdOffset);
        }
        firstIfdOffset -= 8;
        if (firstIfdOffset > 0) {
            if (dataInputStream.skipBytes(firstIfdOffset) != firstIfdOffset) {
                throw new IOException("Couldn't jump to first Ifd: " + firstIfdOffset);
            }
        }
    }
    private void readImageFileDirectory(ByteOrderedDataInputStream dataInputStream,
            @IfdType int ifdType) throws IOException {
        mAttributesOffsets.add(dataInputStream.mPosition);
        if (dataInputStream.mPosition + 2 > dataInputStream.mLength) {
            return;
        }
        short numberOfDirectoryEntry = dataInputStream.readShort();
        if (DEBUG) {
            Log.d(TAG, "numberOfDirectoryEntry: " + numberOfDirectoryEntry);
        }
        if (dataInputStream.mPosition + 12 * numberOfDirectoryEntry > dataInputStream.mLength
                || numberOfDirectoryEntry <= 0) {
            return;
        }
        for (short i = 0; i < numberOfDirectoryEntry; ++i) {
            int tagNumber = dataInputStream.readUnsignedShort();
            int dataFormat = dataInputStream.readUnsignedShort();
            int numberOfComponents = dataInputStream.readInt();
            long nextEntryOffset = dataInputStream.peek() + 4L;
            ExifTag tag = (ExifTag) sExifTagMapsForReading[ifdType].get(tagNumber);
            if (DEBUG) {
                Log.d(TAG, String.format("ifdType: %d, tagNumber: %d, tagName: %s, dataFormat: %d, "
                        + "numberOfComponents: %d", ifdType, tagNumber,
                        tag != null ? tag.name : null, dataFormat, numberOfComponents));
            }
            long byteCount = 0;
            boolean valid = false;
            if (tag == null) {
                Log.w(TAG, "Skip the tag entry since tag number is not defined: " + tagNumber);
            } else if (dataFormat <= 0 || dataFormat >= IFD_FORMAT_BYTES_PER_FORMAT.length) {
                Log.w(TAG, "Skip the tag entry since data format is invalid: " + dataFormat);
            } else if (!tag.isFormatCompatible(dataFormat)) {
                Log.w(TAG, "Skip the tag entry since data format (" + IFD_FORMAT_NAMES[dataFormat]
                        + ") is unexpected for tag: " + tag.name);
            } else {
                if (dataFormat == IFD_FORMAT_UNDEFINED) {
                    dataFormat = tag.primaryFormat;
                }
                byteCount = (long) numberOfComponents * IFD_FORMAT_BYTES_PER_FORMAT[dataFormat];
                if (byteCount < 0 || byteCount > Integer.MAX_VALUE) {
                    Log.w(TAG, "Skip the tag entry since the number of components is invalid: "
                            + numberOfComponents);
                } else {
                    valid = true;
                }
            }
            if (!valid) {
                dataInputStream.seek(nextEntryOffset);
                continue;
            }
            if (byteCount > 4) {
                int offset = dataInputStream.readInt();
                if (DEBUG) {
                    Log.d(TAG, "seek to data offset: " + offset);
                }
                if (mMimeType == IMAGE_TYPE_ORF) {
                    if (TAG_MAKER_NOTE.equals(tag.name)) {
                        mOrfMakerNoteOffset = offset;
                    } else if (ifdType == IFD_TYPE_ORF_MAKER_NOTE
                            && TAG_ORF_THUMBNAIL_IMAGE.equals(tag.name)) {
                        mOrfThumbnailOffset = offset;
                        mOrfThumbnailLength = numberOfComponents;
                        ExifAttribute compressionAttribute =
                                ExifAttribute.createUShort(DATA_JPEG, mExifByteOrder);
                        ExifAttribute jpegInterchangeFormatAttribute =
                                ExifAttribute.createULong(mOrfThumbnailOffset, mExifByteOrder);
                        ExifAttribute jpegInterchangeFormatLengthAttribute =
                                ExifAttribute.createULong(mOrfThumbnailLength, mExifByteOrder);
                        mAttributes[IFD_TYPE_THUMBNAIL].put(TAG_COMPRESSION, compressionAttribute);
                        mAttributes[IFD_TYPE_THUMBNAIL].put(TAG_JPEG_INTERCHANGE_FORMAT,
                                jpegInterchangeFormatAttribute);
                        mAttributes[IFD_TYPE_THUMBNAIL].put(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH,
                                jpegInterchangeFormatLengthAttribute);
                    }
                } else if (mMimeType == IMAGE_TYPE_RW2) {
                    if (TAG_RW2_JPG_FROM_RAW.equals(tag.name)) {
                        mRw2JpgFromRawOffset = offset;
                    }
                }
                if (offset + byteCount <= dataInputStream.mLength) {
                    dataInputStream.seek(offset);
                } else {
                    Log.w(TAG, "Skip the tag entry since data offset is invalid: " + offset);
                    dataInputStream.seek(nextEntryOffset);
                    continue;
                }
            }
            Integer nextIfdType = sExifPointerTagMap.get(tagNumber);
            if (DEBUG) {
                Log.d(TAG, "nextIfdType: " + nextIfdType + " byteCount: " + byteCount);
            }
            if (nextIfdType != null) {
                long offset = -1L;
                switch (dataFormat) {
                    case IFD_FORMAT_USHORT: {
                        offset = dataInputStream.readUnsignedShort();
                        break;
                    }
                    case IFD_FORMAT_SSHORT: {
                        offset = dataInputStream.readShort();
                        break;
                    }
                    case IFD_FORMAT_ULONG: {
                        offset = dataInputStream.readUnsignedInt();
                        break;
                    }
                    case IFD_FORMAT_SLONG:
                    case IFD_FORMAT_IFD: {
                        offset = dataInputStream.readInt();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                if (DEBUG) {
                    Log.d(TAG, String.format("Offset: %d, tagName: %s", offset, tag.name));
                }
                if (offset > 0L && offset < dataInputStream.mLength) {
                    if (!mAttributesOffsets.contains((int) offset)) {
                        dataInputStream.seek(offset);
                        readImageFileDirectory(dataInputStream, nextIfdType);
                    } else {
                        Log.w(TAG, "Skip jump into the IFD since it has already been read: "
                                + "IfdType " + nextIfdType + " (at " + offset + ")");
                    }
                } else {
                    Log.w(TAG, "Skip jump into the IFD since its offset is invalid: " + offset);
                }
                dataInputStream.seek(nextEntryOffset);
                continue;
            }
            final int bytesOffset = dataInputStream.peek();
            final byte[] bytes = new byte[(int) byteCount];
            dataInputStream.readFully(bytes);
            ExifAttribute attribute = new ExifAttribute(dataFormat, numberOfComponents,
                    bytesOffset, bytes);
            mAttributes[ifdType].put(tag.name, attribute);
            if (TAG_DNG_VERSION.equals(tag.name)) {
                mMimeType = IMAGE_TYPE_DNG;
            }
            if (((TAG_MAKE.equals(tag.name) || TAG_MODEL.equals(tag.name))
                    && attribute.getStringValue(mExifByteOrder).contains(PEF_SIGNATURE))
                    || (TAG_COMPRESSION.equals(tag.name)
                    && attribute.getIntValue(mExifByteOrder) == 65535)) {
                mMimeType = IMAGE_TYPE_PEF;
            }
            if (dataInputStream.peek() != nextEntryOffset) {
                dataInputStream.seek(nextEntryOffset);
            }
        }
        if (dataInputStream.peek() + 4 <= dataInputStream.mLength) {
            int nextIfdOffset = dataInputStream.readInt();
            if (DEBUG) {
                Log.d(TAG, String.format("nextIfdOffset: %d", nextIfdOffset));
            }
            if (nextIfdOffset > 0L && nextIfdOffset < dataInputStream.mLength) {
                if (!mAttributesOffsets.contains(nextIfdOffset)) {
                    dataInputStream.seek(nextIfdOffset);
                    if (mAttributes[IFD_TYPE_THUMBNAIL].isEmpty()) {
                        readImageFileDirectory(dataInputStream, IFD_TYPE_THUMBNAIL);
                    } else if (mAttributes[IFD_TYPE_PREVIEW].isEmpty()) {
                        readImageFileDirectory(dataInputStream, IFD_TYPE_PREVIEW);
                    }
                } else {
                    Log.w(TAG, "Stop reading file since re-reading an IFD may cause an "
                            + "infinite loop: " + nextIfdOffset);
                }
            } else {
                Log.w(TAG, "Stop reading file since a wrong offset may cause an infinite loop: "
                        + nextIfdOffset);
            }
        }
    }
    private void retrieveJpegImageSize(ByteOrderedDataInputStream in, int imageType)
            throws IOException {
        ExifAttribute imageLengthAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_IMAGE_LENGTH);
        ExifAttribute imageWidthAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_IMAGE_WIDTH);
        if (imageLengthAttribute == null || imageWidthAttribute == null) {
            ExifAttribute jpegInterchangeFormatAttribute =
                    (ExifAttribute) mAttributes[imageType].get(TAG_JPEG_INTERCHANGE_FORMAT);
            if (jpegInterchangeFormatAttribute != null) {
                int jpegInterchangeFormat =
                        jpegInterchangeFormatAttribute.getIntValue(mExifByteOrder);
                getJpegAttributes(in, jpegInterchangeFormat, imageType);
            }
        }
    }
    private void setThumbnailData(ByteOrderedDataInputStream in) throws IOException {
        HashMap thumbnailData = mAttributes[IFD_TYPE_THUMBNAIL];
        ExifAttribute compressionAttribute =
                (ExifAttribute) thumbnailData.get(TAG_COMPRESSION);
        if (compressionAttribute != null) {
            mThumbnailCompression = compressionAttribute.getIntValue(mExifByteOrder);
            switch (mThumbnailCompression) {
                case DATA_JPEG: {
                    handleThumbnailFromJfif(in, thumbnailData);
                    break;
                }
                case DATA_UNCOMPRESSED:
                case DATA_JPEG_COMPRESSED: {
                    if (isSupportedDataType(thumbnailData)) {
                        handleThumbnailFromStrips(in, thumbnailData);
                    }
                    break;
                }
            }
        } else {
            mThumbnailCompression = DATA_JPEG;
            handleThumbnailFromJfif(in, thumbnailData);
        }
    }
    private void handleThumbnailFromJfif(ByteOrderedDataInputStream in, HashMap thumbnailData)
            throws IOException {
        ExifAttribute jpegInterchangeFormatAttribute =
                (ExifAttribute) thumbnailData.get(TAG_JPEG_INTERCHANGE_FORMAT);
        ExifAttribute jpegInterchangeFormatLengthAttribute =
                (ExifAttribute) thumbnailData.get(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH);
        if (jpegInterchangeFormatAttribute != null
                && jpegInterchangeFormatLengthAttribute != null) {
            int thumbnailOffset = jpegInterchangeFormatAttribute.getIntValue(mExifByteOrder);
            int thumbnailLength = jpegInterchangeFormatLengthAttribute.getIntValue(mExifByteOrder);
            thumbnailLength = Math.min(thumbnailLength, in.getLength() - thumbnailOffset);
            if (mMimeType == IMAGE_TYPE_JPEG || mMimeType == IMAGE_TYPE_RAF
                    || mMimeType == IMAGE_TYPE_RW2) {
                thumbnailOffset += mExifOffset;
            } else if (mMimeType == IMAGE_TYPE_ORF) {
                thumbnailOffset += mOrfMakerNoteOffset;
            }
            if (DEBUG) {
                Log.d(TAG, "Setting thumbnail attributes with offset: " + thumbnailOffset
                        + ", length: " + thumbnailLength);
            }
            if (thumbnailOffset > 0 && thumbnailLength > 0) {
                mHasThumbnail = true;
                mThumbnailOffset = thumbnailOffset;
                mThumbnailLength = thumbnailLength;
                if (mFilename == null && mAssetInputStream == null) {
                    byte[] thumbnailBytes = new byte[thumbnailLength];
                    in.seek(thumbnailOffset);
                    in.readFully(thumbnailBytes);
                    mThumbnailBytes = thumbnailBytes;
                }
            }
        }
    }
    private void handleThumbnailFromStrips(ByteOrderedDataInputStream in, HashMap thumbnailData)
            throws IOException {
        ExifAttribute stripOffsetsAttribute =
                (ExifAttribute) thumbnailData.get(TAG_STRIP_OFFSETS);
        ExifAttribute stripByteCountsAttribute =
                (ExifAttribute) thumbnailData.get(TAG_STRIP_BYTE_COUNTS);
        if (stripOffsetsAttribute != null && stripByteCountsAttribute != null) {
            long[] stripOffsets =
                    convertToLongArray(stripOffsetsAttribute.getValue(mExifByteOrder));
            long[] stripByteCounts =
                    convertToLongArray(stripByteCountsAttribute.getValue(mExifByteOrder));
            if (stripOffsets == null) {
                Log.w(TAG, "stripOffsets should not be null.");
                return;
            }
            if (stripByteCounts == null) {
                Log.w(TAG, "stripByteCounts should not be null.");
                return;
            }
            long totalStripByteCount = 0;
            for (long byteCount : stripByteCounts) {
                totalStripByteCount += byteCount;
            }
            byte[] totalStripBytes = new byte[(int) totalStripByteCount];
            int bytesRead = 0;
            int bytesAdded = 0;
            for (int i = 0; i < stripOffsets.length; i++) {
                int stripOffset = (int) stripOffsets[i];
                int stripByteCount = (int) stripByteCounts[i];
                int skipBytes = stripOffset - bytesRead;
                if (skipBytes < 0) {
                    Log.d(TAG, "Invalid strip offset value");
                }
                in.seek(skipBytes);
                bytesRead += skipBytes;
                byte[] stripBytes = new byte[stripByteCount];
                in.read(stripBytes);
                bytesRead += stripByteCount;
                System.arraycopy(stripBytes, 0, totalStripBytes, bytesAdded,
                        stripBytes.length);
                bytesAdded += stripBytes.length;
            }
            mHasThumbnail = true;
            mThumbnailBytes = totalStripBytes;
            mThumbnailLength = totalStripBytes.length;
        }
    }
    private boolean isSupportedDataType(HashMap thumbnailData) throws IOException {
        ExifAttribute bitsPerSampleAttribute =
                (ExifAttribute) thumbnailData.get(TAG_BITS_PER_SAMPLE);
        if (bitsPerSampleAttribute != null) {
            int[] bitsPerSampleValue = (int[]) bitsPerSampleAttribute.getValue(mExifByteOrder);
            if (Arrays.equals(BITS_PER_SAMPLE_RGB, bitsPerSampleValue)) {
                return true;
            }
            if (mMimeType == IMAGE_TYPE_DNG) {
                ExifAttribute photometricInterpretationAttribute =
                        (ExifAttribute) thumbnailData.get(TAG_PHOTOMETRIC_INTERPRETATION);
                if (photometricInterpretationAttribute != null) {
                    int photometricInterpretationValue
                            = photometricInterpretationAttribute.getIntValue(mExifByteOrder);
                    if ((photometricInterpretationValue == PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO
                            && Arrays.equals(bitsPerSampleValue, BITS_PER_SAMPLE_GREYSCALE_2))
                            || ((photometricInterpretationValue == PHOTOMETRIC_INTERPRETATION_YCBCR)
                            && (Arrays.equals(bitsPerSampleValue, BITS_PER_SAMPLE_RGB)))) {
                        return true;
                    } else {
                    }
                }
            }
        }
        if (DEBUG) {
            Log.d(TAG, "Unsupported data type value");
        }
        return false;
    }
    private boolean isThumbnail(HashMap map) throws IOException {
        ExifAttribute imageLengthAttribute = (ExifAttribute) map.get(TAG_IMAGE_LENGTH);
        ExifAttribute imageWidthAttribute = (ExifAttribute) map.get(TAG_IMAGE_WIDTH);
        if (imageLengthAttribute != null && imageWidthAttribute != null) {
            int imageLengthValue = imageLengthAttribute.getIntValue(mExifByteOrder);
            int imageWidthValue = imageWidthAttribute.getIntValue(mExifByteOrder);
            if (imageLengthValue <= MAX_THUMBNAIL_SIZE && imageWidthValue <= MAX_THUMBNAIL_SIZE) {
                return true;
            }
        }
        return false;
    }
    private void validateImages(InputStream in) throws IOException {
        swapBasedOnImageSize(IFD_TYPE_PRIMARY, IFD_TYPE_PREVIEW);
        swapBasedOnImageSize(IFD_TYPE_PRIMARY, IFD_TYPE_THUMBNAIL);
        swapBasedOnImageSize(IFD_TYPE_PREVIEW, IFD_TYPE_THUMBNAIL);
        ExifAttribute pixelXDimAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_EXIF].get(TAG_PIXEL_X_DIMENSION);
        ExifAttribute pixelYDimAttribute =
                (ExifAttribute) mAttributes[IFD_TYPE_EXIF].get(TAG_PIXEL_Y_DIMENSION);
        if (pixelXDimAttribute != null && pixelYDimAttribute != null) {
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_WIDTH, pixelXDimAttribute);
            mAttributes[IFD_TYPE_PRIMARY].put(TAG_IMAGE_LENGTH, pixelYDimAttribute);
        }
        if (mAttributes[IFD_TYPE_THUMBNAIL].isEmpty()) {
            if (isThumbnail(mAttributes[IFD_TYPE_PREVIEW])) {
                mAttributes[IFD_TYPE_THUMBNAIL] = mAttributes[IFD_TYPE_PREVIEW];
                mAttributes[IFD_TYPE_PREVIEW] = new HashMap<>();
            }
        }
        if (!isThumbnail(mAttributes[IFD_TYPE_THUMBNAIL])) {
            Log.d(TAG, "No image meets the size requirements of a thumbnail image.");
        }
    }
    private void updateImageSizeValues(ByteOrderedDataInputStream in, int imageType)
            throws IOException {
        ExifAttribute defaultCropSizeAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_DEFAULT_CROP_SIZE);
        ExifAttribute topBorderAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_RW2_SENSOR_TOP_BORDER);
        ExifAttribute leftBorderAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_RW2_SENSOR_LEFT_BORDER);
        ExifAttribute bottomBorderAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_RW2_SENSOR_BOTTOM_BORDER);
        ExifAttribute rightBorderAttribute =
                (ExifAttribute) mAttributes[imageType].get(TAG_RW2_SENSOR_RIGHT_BORDER);
        if (defaultCropSizeAttribute != null) {
            ExifAttribute defaultCropSizeXAttribute, defaultCropSizeYAttribute;
            if (defaultCropSizeAttribute.format == IFD_FORMAT_URATIONAL) {
                Rational[] defaultCropSizeValue =
                        (Rational[]) defaultCropSizeAttribute.getValue(mExifByteOrder);
                if (defaultCropSizeValue == null || defaultCropSizeValue.length != 2) {
                    Log.w(TAG, "Invalid crop size values. cropSize="
                            + Arrays.toString(defaultCropSizeValue));
                    return;
                }
                defaultCropSizeXAttribute =
                        ExifAttribute.createURational(defaultCropSizeValue[0], mExifByteOrder);
                defaultCropSizeYAttribute =
                        ExifAttribute.createURational(defaultCropSizeValue[1], mExifByteOrder);
            } else {
                int[] defaultCropSizeValue =
                        (int[]) defaultCropSizeAttribute.getValue(mExifByteOrder);
                if (defaultCropSizeValue == null || defaultCropSizeValue.length != 2) {
                    Log.w(TAG, "Invalid crop size values. cropSize="
                            + Arrays.toString(defaultCropSizeValue));
                    return;
                }
                defaultCropSizeXAttribute =
                        ExifAttribute.createUShort(defaultCropSizeValue[0], mExifByteOrder);
                defaultCropSizeYAttribute =
                        ExifAttribute.createUShort(defaultCropSizeValue[1], mExifByteOrder);
            }
            mAttributes[imageType].put(TAG_IMAGE_WIDTH, defaultCropSizeXAttribute);
            mAttributes[imageType].put(TAG_IMAGE_LENGTH, defaultCropSizeYAttribute);
        } else if (topBorderAttribute != null && leftBorderAttribute != null &&
                bottomBorderAttribute != null && rightBorderAttribute != null) {
            int topBorderValue = topBorderAttribute.getIntValue(mExifByteOrder);
            int bottomBorderValue = bottomBorderAttribute.getIntValue(mExifByteOrder);
            int rightBorderValue = rightBorderAttribute.getIntValue(mExifByteOrder);
            int leftBorderValue = leftBorderAttribute.getIntValue(mExifByteOrder);
            if (bottomBorderValue > topBorderValue && rightBorderValue > leftBorderValue) {
                int length = bottomBorderValue - topBorderValue;
                int width = rightBorderValue - leftBorderValue;
                ExifAttribute imageLengthAttribute =
                        ExifAttribute.createUShort(length, mExifByteOrder);
                ExifAttribute imageWidthAttribute =
                        ExifAttribute.createUShort(width, mExifByteOrder);
                mAttributes[imageType].put(TAG_IMAGE_LENGTH, imageLengthAttribute);
                mAttributes[imageType].put(TAG_IMAGE_WIDTH, imageWidthAttribute);
            }
        } else {
            retrieveJpegImageSize(in, imageType);
        }
    }
    private int writeExifSegment(ByteOrderedDataOutputStream dataOutputStream,
            int exifOffsetFromBeginning) throws IOException {
        int[] ifdOffsets = new int[EXIF_TAGS.length];
        int[] ifdDataSizes = new int[EXIF_TAGS.length];
        for (ExifTag tag : EXIF_POINTER_TAGS) {
            removeAttribute(tag.name);
        }
        removeAttribute(JPEG_INTERCHANGE_FORMAT_TAG.name);
        removeAttribute(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name);
        for (int ifdType = 0; ifdType < EXIF_TAGS.length; ++ifdType) {
            for (Object obj : mAttributes[ifdType].entrySet().toArray()) {
                final Map.Entry entry = (Map.Entry) obj;
                if (entry.getValue() == null) {
                    mAttributes[ifdType].remove(entry.getKey());
                }
            }
        }
        if (!mAttributes[IFD_TYPE_EXIF].isEmpty()) {
            mAttributes[IFD_TYPE_PRIMARY].put(EXIF_POINTER_TAGS[1].name,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (!mAttributes[IFD_TYPE_GPS].isEmpty()) {
            mAttributes[IFD_TYPE_PRIMARY].put(EXIF_POINTER_TAGS[2].name,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (!mAttributes[IFD_TYPE_INTEROPERABILITY].isEmpty()) {
            mAttributes[IFD_TYPE_EXIF].put(EXIF_POINTER_TAGS[3].name,
                    ExifAttribute.createULong(0, mExifByteOrder));
        }
        if (mHasThumbnail) {
            mAttributes[IFD_TYPE_THUMBNAIL].put(JPEG_INTERCHANGE_FORMAT_TAG.name,
                    ExifAttribute.createULong(0, mExifByteOrder));
            mAttributes[IFD_TYPE_THUMBNAIL].put(JPEG_INTERCHANGE_FORMAT_LENGTH_TAG.name,
                    ExifAttribute.createULong(mThumbnailLength, mExifByteOrder));
        }
        for (int i = 0; i < EXIF_TAGS.length; ++i) {
            int sum = 0;
            for (Map.Entry<String, ExifAttribute> entry : mAttributes[i].entrySet()) {
                final ExifAttribute exifAttribute = entry.getValue();
                final int size = exifAttribute.size();
                if (size > 4) {
                    sum += size;
                }
            }
            ifdDataSizes[i] += sum;
        }
        int position = 8;
        for (int ifdType = 0; ifdType < EXIF_TAGS.length; ++ifdType) {
            if (!mAttributes[ifdType].isEmpty()) {
                ifdOffsets[ifdType] = position;
                position += 2 + mAttributes[ifdType].size() * 12 + 4 + ifdDataSizes[ifdType];
            }
        }
        if (mHasThumbnail) {
            int thumbnailOffset = position;
            mAttributes[IFD_TYPE_THUMBNAIL].put(JPEG_INTERCHANGE_FORMAT_TAG.name,
                    ExifAttribute.createULong(thumbnailOffset, mExifByteOrder));
            mThumbnailOffset = exifOffsetFromBeginning + thumbnailOffset;
            position += mThumbnailLength;
        }
        int totalSize = position + 8;  // eight bytes is for header part.
        if (DEBUG) {
            Log.d(TAG, "totalSize length: " + totalSize);
            for (int i = 0; i < EXIF_TAGS.length; ++i) {
                Log.d(TAG, String.format("index: %d, offsets: %d, tag count: %d, data sizes: %d",
                        i, ifdOffsets[i], mAttributes[i].size(), ifdDataSizes[i]));
            }
        }
        if (!mAttributes[IFD_TYPE_EXIF].isEmpty()) {
            mAttributes[IFD_TYPE_PRIMARY].put(EXIF_POINTER_TAGS[1].name,
                    ExifAttribute.createULong(ifdOffsets[IFD_TYPE_EXIF], mExifByteOrder));
        }
        if (!mAttributes[IFD_TYPE_GPS].isEmpty()) {
            mAttributes[IFD_TYPE_PRIMARY].put(EXIF_POINTER_TAGS[2].name,
                    ExifAttribute.createULong(ifdOffsets[IFD_TYPE_GPS], mExifByteOrder));
        }
        if (!mAttributes[IFD_TYPE_INTEROPERABILITY].isEmpty()) {
            mAttributes[IFD_TYPE_EXIF].put(EXIF_POINTER_TAGS[3].name, ExifAttribute.createULong(
                    ifdOffsets[IFD_TYPE_INTEROPERABILITY], mExifByteOrder));
        }
        dataOutputStream.writeUnsignedShort(totalSize);
        dataOutputStream.write(IDENTIFIER_EXIF_APP1);
        dataOutputStream.writeShort(mExifByteOrder == ByteOrder.BIG_ENDIAN
                ? BYTE_ALIGN_MM : BYTE_ALIGN_II);
        dataOutputStream.setByteOrder(mExifByteOrder);
        dataOutputStream.writeUnsignedShort(START_CODE);
        dataOutputStream.writeUnsignedInt(IFD_OFFSET);
        for (int ifdType = 0; ifdType < EXIF_TAGS.length; ++ifdType) {
            if (!mAttributes[ifdType].isEmpty()) {
                dataOutputStream.writeUnsignedShort(mAttributes[ifdType].size());
                int dataOffset = ifdOffsets[ifdType] + 2 + mAttributes[ifdType].size() * 12 + 4;
                for (Map.Entry<String, ExifAttribute> entry : mAttributes[ifdType].entrySet()) {
                    final ExifTag tag = sExifTagMapsForWriting[ifdType].get(entry.getKey());
                    final int tagNumber = tag.number;
                    final ExifAttribute attribute = entry.getValue();
                    final int size = attribute.size();
                    dataOutputStream.writeUnsignedShort(tagNumber);
                    dataOutputStream.writeUnsignedShort(attribute.format);
                    dataOutputStream.writeInt(attribute.numberOfComponents);
                    if (size > 4) {
                        dataOutputStream.writeUnsignedInt(dataOffset);
                        dataOffset += size;
                    } else {
                        dataOutputStream.write(attribute.bytes);
                        if (size < 4) {
                            for (int i = size; i < 4; ++i) {
                                dataOutputStream.writeByte(0);
                            }
                        }
                    }
                }
                if (ifdType == 0 && !mAttributes[IFD_TYPE_THUMBNAIL].isEmpty()) {
                    dataOutputStream.writeUnsignedInt(ifdOffsets[IFD_TYPE_THUMBNAIL]);
                } else {
                    dataOutputStream.writeUnsignedInt(0);
                }
                for (Map.Entry<String, ExifAttribute> entry : mAttributes[ifdType].entrySet()) {
                    ExifAttribute attribute = entry.getValue();
                    if (attribute.bytes.length > 4) {
                        dataOutputStream.write(attribute.bytes, 0, attribute.bytes.length);
                    }
                }
            }
        }
        if (mHasThumbnail) {
            dataOutputStream.write(getThumbnailBytes());
        }
        dataOutputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
        return totalSize;
    }
               data formats for the given entry value, returns {@code -1} in the second of the pair.
    private static Pair<Integer, Integer> guessDataFormat(String entryValue) {
        if (entryValue.contains(",")) {
            String[] entryValues = entryValue.split(",", -1);
            Pair<Integer, Integer> dataFormat = guessDataFormat(entryValues[0]);
            if (dataFormat.first == IFD_FORMAT_STRING) {
                return dataFormat;
            }
            for (int i = 1; i < entryValues.length; ++i) {
                final Pair<Integer, Integer> guessDataFormat = guessDataFormat(entryValues[i]);
                int first = -1, second = -1;
                if (guessDataFormat.first.equals(dataFormat.first)
                        || guessDataFormat.second.equals(dataFormat.first)) {
                    first = dataFormat.first;
                }
                if (dataFormat.second != -1 && (guessDataFormat.first.equals(dataFormat.second)
                        || guessDataFormat.second.equals(dataFormat.second))) {
                    second = dataFormat.second;
                }
                if (first == -1 && second == -1) {
                    return new Pair<>(IFD_FORMAT_STRING, -1);
                }
                if (first == -1) {
                    dataFormat = new Pair<>(second, -1);
                    continue;
                }
                if (second == -1) {
                    dataFormat = new Pair<>(first, -1);
                    continue;
                }
            }
            return dataFormat;
        }
        if (entryValue.contains("/")) {
            String[] rationalNumber = entryValue.split("/", -1);
            if (rationalNumber.length == 2) {
                try {
                    long numerator = (long) Double.parseDouble(rationalNumber[0]);
                    long denominator = (long) Double.parseDouble(rationalNumber[1]);
                    if (numerator < 0L || denominator < 0L) {
                        return new Pair<>(IFD_FORMAT_SRATIONAL, -1);
                    }
                    if (numerator > Integer.MAX_VALUE || denominator > Integer.MAX_VALUE) {
                        return new Pair<>(IFD_FORMAT_URATIONAL, -1);
                    }
                    return new Pair<>(IFD_FORMAT_SRATIONAL, IFD_FORMAT_URATIONAL);
                } catch (NumberFormatException e)  {
                }
            }
            return new Pair<>(IFD_FORMAT_STRING, -1);
        }
        try {
            Long longValue = Long.parseLong(entryValue);
            if (longValue >= 0 && longValue <= 65535) {
                return new Pair<>(IFD_FORMAT_USHORT, IFD_FORMAT_ULONG);
            }
            if (longValue < 0) {
                return new Pair<>(IFD_FORMAT_SLONG, -1);
            }
            return new Pair<>(IFD_FORMAT_ULONG, -1);
        } catch (NumberFormatException e) {
        }
        try {
            Double.parseDouble(entryValue);
            return new Pair<>(IFD_FORMAT_DOUBLE, -1);
        } catch (NumberFormatException e) {
        }
        return new Pair<>(IFD_FORMAT_STRING, -1);
    }
    private static class ByteOrderedDataInputStream extends InputStream implements DataInput {
        private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
        private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
        private DataInputStream mDataInputStream;
        private ByteOrder mByteOrder = ByteOrder.BIG_ENDIAN;
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        final int mLength;
        @SuppressWarnings("WeakerAccess") /* synthetic access */
        int mPosition;
        public ByteOrderedDataInputStream(InputStream in) throws IOException {
            mDataInputStream = new DataInputStream(in);
            mLength = mDataInputStream.available();
            mPosition = 0;
            mDataInputStream.mark(mLength);
        }
        public ByteOrderedDataInputStream(byte[] bytes) throws IOException {
            this(new ByteArrayInputStream(bytes));
        }
        public void setByteOrder(ByteOrder byteOrder) {
            mByteOrder = byteOrder;
        }
        public void seek(long byteCount) throws IOException {
            if (mPosition > byteCount) {
                mPosition = 0;
                mDataInputStream.reset();
                mDataInputStream.mark(mLength);
            } else {
                byteCount -= mPosition;
            }
            if (skipBytes((int) byteCount) != (int) byteCount) {
                throw new IOException("Couldn't seek up to the byteCount");
            }
        }
        public int peek() {
            return mPosition;
        }
        @Override
        public int available() throws IOException {
            return mDataInputStream.available();
        }
        @Override
        public int read() throws IOException {
            ++mPosition;
            return mDataInputStream.read();
        }
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesRead = mDataInputStream.read(b, off, len);
            mPosition += bytesRead;
            return bytesRead;
        }
        @Override
        public int readUnsignedByte() throws IOException {
            ++mPosition;
            return mDataInputStream.readUnsignedByte();
        }
        @Override
        public String readLine() throws IOException {
            Log.d(TAG, "Currently unsupported");
            return null;
        }
        @Override
        public boolean readBoolean() throws IOException {
            ++mPosition;
            return mDataInputStream.readBoolean();
        }
        @Override
        public char readChar() throws IOException {
            mPosition += 2;
            return mDataInputStream.readChar();
        }
        @Override
        public String readUTF() throws IOException {
            mPosition += 2;
            return mDataInputStream.readUTF();
        }
        @Override
        public void readFully(byte[] buffer, int offset, int length) throws IOException {
            mPosition += length;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            if (mDataInputStream.read(buffer, offset, length) != length) {
                throw new IOException("Couldn't read up to the length of buffer");
            }
        }
        @Override
        public void readFully(byte[] buffer) throws IOException {
            mPosition += buffer.length;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            if (mDataInputStream.read(buffer, 0, buffer.length) != buffer.length) {
                throw new IOException("Couldn't read up to the length of buffer");
            }
        }
        @Override
        public byte readByte() throws IOException {
            ++mPosition;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            int ch = mDataInputStream.read();
            if (ch < 0) {
                throw new EOFException();
            }
            return (byte) ch;
        }
        @Override
        public short readShort() throws IOException {
            mPosition += 2;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            int ch1 = mDataInputStream.read();
            int ch2 = mDataInputStream.read();
            if ((ch1 | ch2) < 0) {
                throw new EOFException();
            }
            if (mByteOrder == LITTLE_ENDIAN) {
                return (short) ((ch2 << 8) + (ch1));
            } else if (mByteOrder == BIG_ENDIAN) {
                return (short) ((ch1 << 8) + (ch2));
            }
            throw new IOException("Invalid byte order: " + mByteOrder);
        }
        @Override
        public int readInt() throws IOException {
            mPosition += 4;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            int ch1 = mDataInputStream.read();
            int ch2 = mDataInputStream.read();
            int ch3 = mDataInputStream.read();
            int ch4 = mDataInputStream.read();
            if ((ch1 | ch2 | ch3 | ch4) < 0) {
                throw new EOFException();
            }
            if (mByteOrder == LITTLE_ENDIAN) {
                return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1);
            } else if (mByteOrder == BIG_ENDIAN) {
                return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4);
            }
            throw new IOException("Invalid byte order: " + mByteOrder);
        }
        @Override
        public int skipBytes(int byteCount) throws IOException {
            int totalSkip = Math.min(byteCount, mLength - mPosition);
            int skipped = 0;
            while (skipped < totalSkip) {
                skipped += mDataInputStream.skipBytes(totalSkip - skipped);
            }
            mPosition += skipped;
            return skipped;
        }
        @Override
        public int readUnsignedShort() throws IOException {
            mPosition += 2;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            int ch1 = mDataInputStream.read();
            int ch2 = mDataInputStream.read();
            if ((ch1 | ch2) < 0) {
                throw new EOFException();
            }
            if (mByteOrder == LITTLE_ENDIAN) {
                return ((ch2 << 8) + (ch1));
            } else if (mByteOrder == BIG_ENDIAN) {
                return ((ch1 << 8) + (ch2));
            }
            throw new IOException("Invalid byte order: " + mByteOrder);
        }
        public long readUnsignedInt() throws IOException {
            return readInt() & 0xffffffffL;
        }
        @Override
        public long readLong() throws IOException {
            mPosition += 8;
            if (mPosition > mLength) {
                throw new EOFException();
            }
            int ch1 = mDataInputStream.read();
            int ch2 = mDataInputStream.read();
            int ch3 = mDataInputStream.read();
            int ch4 = mDataInputStream.read();
            int ch5 = mDataInputStream.read();
            int ch6 = mDataInputStream.read();
            int ch7 = mDataInputStream.read();
            int ch8 = mDataInputStream.read();
            if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
                throw new EOFException();
            }
            if (mByteOrder == LITTLE_ENDIAN) {
                return (((long) ch8 << 56) + ((long) ch7 << 48) + ((long) ch6 << 40)
                        + ((long) ch5 << 32) + ((long) ch4 << 24) + ((long) ch3 << 16)
                        + ((long) ch2 << 8) + (long) ch1);
            } else if (mByteOrder == BIG_ENDIAN) {
                return (((long) ch1 << 56) + ((long) ch2 << 48) + ((long) ch3 << 40)
                        + ((long) ch4 << 32) + ((long) ch5 << 24) + ((long) ch6 << 16)
                        + ((long) ch7 << 8) + (long) ch8);
            }
            throw new IOException("Invalid byte order: " + mByteOrder);
        }
        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readInt());
        }
        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readLong());
        }
        public int getLength() {
            return mLength;
        }
    }
    private static class ByteOrderedDataOutputStream extends FilterOutputStream {
        private final OutputStream mOutputStream;
        private ByteOrder mByteOrder;
        public ByteOrderedDataOutputStream(OutputStream out, ByteOrder byteOrder) {
            super(out);
            mOutputStream = out;
            mByteOrder = byteOrder;
        }
        public void setByteOrder(ByteOrder byteOrder) {
            mByteOrder = byteOrder;
        }
        @Override
        public void write(byte[] bytes) throws IOException {
            mOutputStream.write(bytes);
        }
        @Override
        public void write(byte[] bytes, int offset, int length) throws IOException {
            mOutputStream.write(bytes, offset, length);
        }
        public void writeByte(int val) throws IOException {
            mOutputStream.write(val);
        }
        public void writeShort(short val) throws IOException {
            if (mByteOrder == ByteOrder.LITTLE_ENDIAN) {
                mOutputStream.write((val >>> 0) & 0xFF);
                mOutputStream.write((val >>> 8) & 0xFF);
            } else if (mByteOrder == ByteOrder.BIG_ENDIAN) {
                mOutputStream.write((val >>> 8) & 0xFF);
                mOutputStream.write((val >>> 0) & 0xFF);
            }
        }
        public void writeInt(int val) throws IOException {
            if (mByteOrder == ByteOrder.LITTLE_ENDIAN) {
                mOutputStream.write((val >>> 0) & 0xFF);
                mOutputStream.write((val >>> 8) & 0xFF);
                mOutputStream.write((val >>> 16) & 0xFF);
                mOutputStream.write((val >>> 24) & 0xFF);
            } else if (mByteOrder == ByteOrder.BIG_ENDIAN) {
                mOutputStream.write((val >>> 24) & 0xFF);
                mOutputStream.write((val >>> 16) & 0xFF);
                mOutputStream.write((val >>> 8) & 0xFF);
                mOutputStream.write((val >>> 0) & 0xFF);
            }
        }
        public void writeUnsignedShort(int val) throws IOException {
            writeShort((short) val);
        }
        public void writeUnsignedInt(long val) throws IOException {
            writeInt((int) val);
        }
    }
    private void swapBasedOnImageSize(@IfdType int firstIfdType, @IfdType int secondIfdType)
            throws IOException {
        if (mAttributes[firstIfdType].isEmpty() || mAttributes[secondIfdType].isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "Cannot perform swap since only one image data exists");
            }
            return;
        }
        ExifAttribute firstImageLengthAttribute =
                (ExifAttribute) mAttributes[firstIfdType].get(TAG_IMAGE_LENGTH);
        ExifAttribute firstImageWidthAttribute =
                (ExifAttribute) mAttributes[firstIfdType].get(TAG_IMAGE_WIDTH);
        ExifAttribute secondImageLengthAttribute =
                (ExifAttribute) mAttributes[secondIfdType].get(TAG_IMAGE_LENGTH);
        ExifAttribute secondImageWidthAttribute =
                (ExifAttribute) mAttributes[secondIfdType].get(TAG_IMAGE_WIDTH);
        if (firstImageLengthAttribute == null || firstImageWidthAttribute == null) {
            if (DEBUG) {
                Log.d(TAG, "First image does not contain valid size information");
            }
        } else if (secondImageLengthAttribute == null || secondImageWidthAttribute == null) {
            if (DEBUG) {
                Log.d(TAG, "Second image does not contain valid size information");
            }
        } else {
            int firstImageLengthValue = firstImageLengthAttribute.getIntValue(mExifByteOrder);
            int firstImageWidthValue = firstImageWidthAttribute.getIntValue(mExifByteOrder);
            int secondImageLengthValue = secondImageLengthAttribute.getIntValue(mExifByteOrder);
            int secondImageWidthValue = secondImageWidthAttribute.getIntValue(mExifByteOrder);
            if (firstImageLengthValue < secondImageLengthValue &&
                    firstImageWidthValue < secondImageWidthValue) {
                HashMap<String, ExifAttribute> tempMap = mAttributes[firstIfdType];
                mAttributes[firstIfdType] = mAttributes[secondIfdType];
                mAttributes[secondIfdType] = tempMap;
            }
        }
    }
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
    private static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[8192];
        int c;
        while ((c = in.read(buffer)) != -1) {
            total += c;
            out.write(buffer, 0, c);
        }
        return total;
    }
    private static long[] convertToLongArray(Object inputObj) {
        if (inputObj instanceof int[]) {
            int[] input = (int[]) inputObj;
            long[] result = new long[input.length];
            for (int i = 0; i < input.length; i++) {
                result[i] = input[i];
            }
            return result;
        } else if (inputObj instanceof long[]) {
            return (long[]) inputObj;
        }
        return null;
    }
    private static boolean startsWith(byte[] cur, byte[] val) {
        if (cur == null || val == null) {
            return false;
        }
        if (cur.length < val.length) {
            return false;
        }
        for (int i = 0; i < val.length; i++) {
            if (cur[i] != val[i]) {
                return false;
            }
        }
        return true;
    }
}

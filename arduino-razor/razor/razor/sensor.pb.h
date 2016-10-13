/* Automatically generated nanopb header */
/* Generated by nanopb-0.3.6 at Wed Oct 12 17:51:43 2016. */

#ifndef PB_SENSOR_PB_H_INCLUDED
#define PB_SENSOR_PB_H_INCLUDED
#include <pb.h>

/* @@protoc_insertion_point(includes) */
#if PB_PROTO_HEADER_VERSION != 30
#error Regenerate this file with the current version of nanopb generator.
#endif

#ifdef __cplusplus
extern "C" {
#endif

/* Struct definitions */
typedef struct _IMUData {
    int32_t ax;
    int32_t ay;
    int32_t az;
    int32_t gx;
    int32_t gy;
    int32_t gz;
    float yaw;
    float pitch;
    float roll;
    int32_t temperature;
/* @@protoc_insertion_point(struct:IMUData) */
} IMUData;

typedef struct _FootSensorData {
    IMUData imu_data;
    int32_t pressure_front;
/* @@protoc_insertion_point(struct:FootSensorData) */
} FootSensorData;

typedef struct _SensorData {
    FootSensorData left;
    FootSensorData right;
/* @@protoc_insertion_point(struct:SensorData) */
} SensorData;

/* Default values for struct fields */

/* Initializer values for message structs */
#define IMUData_init_default                     {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
#define FootSensorData_init_default              {IMUData_init_default, 0}
#define SensorData_init_default                  {FootSensorData_init_default, FootSensorData_init_default}
#define IMUData_init_zero                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
#define FootSensorData_init_zero                 {IMUData_init_zero, 0}
#define SensorData_init_zero                     {FootSensorData_init_zero, FootSensorData_init_zero}

/* Field tags (for use in manual encoding/decoding) */
#define IMUData_ax_tag                           1
#define IMUData_ay_tag                           2
#define IMUData_az_tag                           3
#define IMUData_gx_tag                           4
#define IMUData_gy_tag                           5
#define IMUData_gz_tag                           6
#define IMUData_yaw_tag                          7
#define IMUData_pitch_tag                        8
#define IMUData_roll_tag                         9
#define IMUData_temperature_tag                  10
#define FootSensorData_imu_data_tag              1
#define FootSensorData_pressure_front_tag        2
#define SensorData_left_tag                      1
#define SensorData_right_tag                     2

/* Struct field encoding specification for nanopb */
extern const pb_field_t IMUData_fields[11];
extern const pb_field_t FootSensorData_fields[3];
extern const pb_field_t SensorData_fields[3];

/* Maximum encoded size of messages (where known) */
#define IMUData_size                             92
#define FootSensorData_size                      105
#define SensorData_size                          214

/* Message IDs (where set with "msgid" option) */
#ifdef PB_MSGID

#define SENSOR_MESSAGES \


#endif

#ifdef __cplusplus
} /* extern "C" */
#endif
/* @@protoc_insertion_point(eof) */

#endif

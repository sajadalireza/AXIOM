#!/usr/bin/env python3
"""Read androidx DataStore Preferences protobuf files (read-only).

Prints simple string/boolean/int preferences entries, e.g.:
    language=fa
    theme_mode=SYSTEM

Usage: read_datastore_prefs.py <path-to-preferences_pb>
"""
import sys


def read_varint(buf, i):
    shift = 0
    value = 0
    while True:
        b = buf[i]
        i += 1
        value |= (b & 0x7F) << shift
        if not (b & 0x80):
            return value, i
        shift += 7


def parse_message(buf):
    i = 0
    fields = []
    while i < len(buf):
        key, i = read_varint(buf, i)
        field, wire = key >> 3, key & 7
        if wire == 0:
            value, i = read_varint(buf, i)
        elif wire == 2:
            length, i = read_varint(buf, i)
            value = buf[i:i + length]
            i += length
        elif wire == 5:
            value = buf[i:i + 4]
            i += 4
        elif wire == 1:
            value = buf[i:i + 8]
            i += 8
        else:
            raise ValueError(f"unsupported wire type {wire}")
        fields.append((field, wire, value))
    return fields


def main():
    data = open(sys.argv[1], "rb").read()
    for field, wire, entry in parse_message(data):
        if field != 1 or wire != 2:
            continue
        key = None
        value = None
        for f2, w2, v2 in parse_message(entry):
            if f2 == 1 and w2 == 2:
                key = v2.decode("utf-8", "replace")
            elif f2 == 2 and w2 == 2:
                for f3, w3, v3 in parse_message(v2):
                    if w3 == 2:
                        value = v3.decode("utf-8", "replace")
                    elif w3 == 0:
                        value = v3
        if key is not None:
            print(f"{key}={value}")


if __name__ == "__main__":
    main()

import React, { useMemo } from 'react';
import * as THREE from 'three';
import { scaleCoords } from '../utils/coordinates';
import { jsx as _jsx } from "react/jsx-runtime";
export const WardGeometry = ({
  geometry,
  color
}) => {
  const mesh = useMemo(() => {
    if (!geometry || geometry.type !== 'Polygon') return null;
    const shape = new THREE.Shape();
    const points = geometry.coordinates[0];
    points.forEach((coord, i) => {
      const [x,, z] = scaleCoords(coord);
      if (i === 0) shape.moveTo(x, z);else shape.lineTo(x, z);
    });
    const extrudeSettings = {
      steps: 1,
      depth: 0.1,
      bevelEnabled: true,
      bevelThickness: 0.05,
      bevelSize: 0.05
    };
    return new THREE.ExtrudeGeometry(shape, extrudeSettings);
  }, [geometry]);
  if (!mesh) return null;
  return /*#__PURE__*/_jsx("mesh", {
    geometry: mesh,
    rotation: [-Math.PI / 2, 0, 0],
    castShadow: true,
    receiveShadow: true,
    children: /*#__PURE__*/_jsx("meshStandardMaterial", {
      color: color,
      roughness: 0.3,
      metalness: 0.2
    })
  });
};
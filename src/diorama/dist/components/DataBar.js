import React from 'react';
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
export const DataBar = ({
  value,
  max = 100,
  color = '#3498db'
}) => {
  const height = value / max * 5;
  return /*#__PURE__*/_jsxs("mesh", {
    position: [0, height / 2, 0],
    castShadow: true,
    children: [/*#__PURE__*/_jsx("boxGeometry", {
      args: [0.2, height, 0.2]
    }), /*#__PURE__*/_jsx("meshStandardMaterial", {
      color: color,
      emissive: color,
      emissiveIntensity: 0.5,
      transparent: true,
      opacity: 0.8
    })]
  });
};
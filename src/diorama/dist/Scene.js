import React from 'react';
import { Stars, Environment, SoftShadows } from '@react-three/drei';
import { jsx as _jsx, Fragment as _Fragment, jsxs as _jsxs } from "react/jsx-runtime";
export const Scene = ({
  options
}) => {
  return /*#__PURE__*/_jsxs(_Fragment, {
    children: [/*#__PURE__*/_jsx(SoftShadows, {
      size: 15,
      focus: 1.5,
      samples: 10
    }), /*#__PURE__*/_jsx("ambientLight", {
      intensity: 0.5
    }), /*#__PURE__*/_jsx("directionalLight", {
      castShadow: true,
      position: [10, 20, 10],
      intensity: 1.5,
      "shadow-mapSize": [1024, 1024],
      children: /*#__PURE__*/_jsx("orthographicCamera", {
        attach: "shadow-camera",
        args: [-20, 20, 20, -20]
      })
    }), options.theme === 'dark' && /*#__PURE__*/_jsx(Stars, {
      radius: 100,
      depth: 50,
      count: 5000,
      factor: 4,
      saturation: 0,
      fade: true,
      speed: 1
    }), /*#__PURE__*/_jsx(Environment, {
      preset: "city"
    }), /*#__PURE__*/_jsx("gridHelper", {
      args: [100, 50, '#444', '#222'],
      position: [0, -0.01, 0]
    })]
  });
};
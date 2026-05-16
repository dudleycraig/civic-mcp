import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Sky, ContactShadows } from '@react-three/drei';
import { Scene } from './Scene';
import { World } from './World';
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
export default function Diorama({
  data,
  options
}) {
  return /*#__PURE__*/_jsxs(Canvas, {
    shadows: true,
    camera: {
      position: [0, 5, 10],
      fov: 50
    },
    children: [/*#__PURE__*/_jsx("color", {
      attach: "background",
      args: [options.theme === 'dark' ? '#111' : '#eee']
    }), /*#__PURE__*/_jsxs(Suspense, {
      fallback: null,
      children: [/*#__PURE__*/_jsx(Scene, {
        options: options
      }), /*#__PURE__*/_jsx(World, {
        data: data
      }), /*#__PURE__*/_jsx(Sky, {
        sunPosition: [100, 20, 100]
      }), /*#__PURE__*/_jsx(ContactShadows, {
        opacity: 0.5,
        scale: 10,
        blur: 1,
        far: 10
      })]
    }), /*#__PURE__*/_jsx(OrbitControls, {
      makeDefault: true
    })]
  });
}
;
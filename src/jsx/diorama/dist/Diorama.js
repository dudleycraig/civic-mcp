import React, { Suspense } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, PerspectiveCamera } from '@react-three/drei';
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
export default function Diorama({
  children,
  options
}) {
  return /*#__PURE__*/_jsxs(Canvas, {
    shadows: true,
    dpr: [1, 2],
    gl: {
      antialias: true
    },
    children: [/*#__PURE__*/_jsx("color", {
      attach: "background",
      args: [options.theme === 'dark' ? '#111' : '#eee']
    }), /*#__PURE__*/_jsx(PerspectiveCamera, {
      makeDefault: true,
      position: [0, 5, 10],
      fov: 50
    }), /*#__PURE__*/_jsx("ambientLight", {
      intensity: 0.5
    }), /*#__PURE__*/_jsx("directionalLight", {
      position: [10, 10, 5],
      intensity: 1,
      castShadow: true
    }), /*#__PURE__*/_jsx(Suspense, {
      fallback: null,
      children: children
    }), /*#__PURE__*/_jsx(OrbitControls, {
      makeDefault: true
    })]
  });
}
import React, { useMemo } from 'react';
import { WardGeometry } from './components/WardGeometry';
import { DataBar } from './components/DataBar';
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
export const World = ({
  data
}) => {
  const wards = useMemo(() => data?.wards || [], [data]);
  return /*#__PURE__*/_jsx("group", {
    name: "za-world",
    children: wards.map(ward => /*#__PURE__*/_jsxs("group", {
      position: ward.position || [0, 0, 0],
      children: [/*#__PURE__*/_jsx(WardGeometry, {
        geometry: ward.geometry,
        color: ward.color || '#666'
      }), ward.stats && /*#__PURE__*/_jsx(DataBar, {
        value: ward.stats.value,
        max: ward.stats.max,
        color: ward.stats.color
      })]
    }, ward.id))
  });
};
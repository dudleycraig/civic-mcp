import React from 'react';

export const DataBar = ({ value, max = 100, color = '#3498db' }) => {
  const height = (value / max) * 5;

  return (
    <mesh position={[0, height / 2, 0]} castShadow>
      <boxGeometry args={[0.2, height, 0.2]} />
      <meshStandardMaterial color={color} emissive={color} emissiveIntensity={0.5} transparent opacity={0.8} />
    </mesh>
  );
}

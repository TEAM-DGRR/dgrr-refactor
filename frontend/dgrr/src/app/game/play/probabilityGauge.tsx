// ProbabilityGauge.tsx
'use client';
import './probabilityGauge.scss';

interface Props {
  probability: number;
}

const ProbabilityGauge: React.FC<Props> = ({ probability }) => {
  const percentage = Math.round(probability * 100);

  return (
    <div className='gauge-container'>
      <div className='gauge-bar' style={{ width: `${percentage}%` }} />
      <span className='gauge-label'>{percentage}%</span>
    </div>
  );
};

export default ProbabilityGauge;

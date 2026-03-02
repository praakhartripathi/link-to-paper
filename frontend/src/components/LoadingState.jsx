import React, { useEffect, useState } from 'react';

const STEPS = [
    { id: 1, label: 'Fetching webpage…', icon: '🌐' },
    { id: 2, label: 'Cleaning content…', icon: '🧹' },
    { id: 3, label: 'AI processing…', icon: '🤖' },
    { id: 4, label: 'Generating PDF…', icon: '📄' },
    { id: 5, label: 'Preparing download…', icon: '⬇️' },
];

// Simulated durations in ms (just for UX — real completion comes from the API)
const STEP_DELAYS = [800, 1200, 4000, 1500, 500];

export default function LoadingState() {
    const [currentStep, setCurrentStep] = useState(0);

    useEffect(() => {
        let step = 0;
        let timer;
        const advance = () => {
            step += 1;
            if (step < STEPS.length) {
                setCurrentStep(step);
                timer = setTimeout(advance, STEP_DELAYS[step]);
            }
        };
        timer = setTimeout(advance, STEP_DELAYS[0]);
        return () => clearTimeout(timer);
    }, []);

    return (
        <div className="w-full max-w-lg mx-auto animate-fade-in">
            <div className="glass-card rounded-2xl p-8 flex flex-col items-center gap-6">
                {/* Animated spinner ring */}
                <div className="relative">
                    <div className="spinner" />
                    <div className="absolute inset-0 flex items-center justify-center text-xl">
                        {STEPS[currentStep]?.icon}
                    </div>
                </div>

                {/* Current step label */}
                <div className="text-center">
                    <p className="text-slate-200 font-semibold text-base">
                        {STEPS[currentStep]?.label}
                    </p>
                    <p className="text-slate-500 text-xs mt-1">This may take up to 30 seconds</p>
                </div>

                {/* Step progress dots */}
                <div className="flex items-center gap-2">
                    {STEPS.map((s, i) => (
                        <React.Fragment key={s.id}>
                            <div
                                className={`step-dot ${i < currentStep ? 'done' :
                                        i === currentStep ? 'active' : ''
                                    }`}
                            />
                            {i < STEPS.length - 1 && (
                                <div className={`h-px w-6 transition-all duration-500 ${i < currentStep ? 'bg-green-500' : 'bg-slate-700'
                                    }`} />
                            )}
                        </React.Fragment>
                    ))}
                </div>

                {/* Active step labels list */}
                <ul className="text-xs space-y-1.5 self-stretch">
                    {STEPS.map((s, i) => (
                        <li key={s.id} className={`flex items-center gap-2 transition-all duration-300 ${i < currentStep ? 'text-green-400' :
                                i === currentStep ? 'text-indigo-300' :
                                    'text-slate-600'
                            }`}>
                            <span>{i < currentStep ? '✓' : i === currentStep ? '›' : '·'}</span>
                            {s.label}
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}

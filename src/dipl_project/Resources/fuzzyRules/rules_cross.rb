!COMMENT!
Enter your description of the rulebase here.
!END_COMMENT!

TypeOfDescription=linguistic
InfMethod=Fuzzy_Approximation-functional
DefuzzMethod=SimpleCenterOfGravity
UseFuzzyFilter=false

NumberOfAntecedentVariables=2
NumberOfSuccedentVariables=1
NumberOfRules=20

AntVariable1
 name=dSpeed
 settings=new
 context=<-7,-5,0,5,7>
 discretization=301
 discretization_left=301
 UserTerm
  name=very slow
  type=trapezoid
  parameters= -7 -7 -6.3 -3.5
 End_UserTerm
 UserTerm
  name=slow
  type=trapezoid
  parameters= -6.3 -3.5 -2.8 0
 End_UserTerm
 UserTerm
  name=equal
  type=triang
  parameters= -2.8 0 2.8
 End_UserTerm
 UserTerm
  name=fast
  type=trapezoid
  parameters= 0 2.8 3.5 6.3
 End_UserTerm
 UserTerm
  name=very fast
  type=trapezoid
  parameters= 3.5 6.3 7 7
 End_UserTerm
End_AntVariable1

AntVariable2
 name=dDistance
 settings=new
 context=<-10,-5,0,5,10>
 discretization=301
 discretization_left=301
 UserTerm
  name=equal
  type=trapezoid
  parameters= -4 -2 2 4
 End_UserTerm
 UserTerm
  name=close
  type=trapezoid
  parameters= -9 -6 -4 -2
 End_UserTerm
 UserTerm
  name=far
  type=trapezoid
  parameters= 2 4 10 10
 End_UserTerm
 UserTerm
  name=too close
  type=trapezoid
  parameters= -10 -10 -9 -6
 End_UserTerm
End_AntVariable2

SucVariable1
 name=run
 settings=new
 context=<0,1,2>
 discretization=51
 UserTerm
  name=stop
  type=triang
  parameters= 0 0 2
 End_UserTerm
 UserTerm
  name=run
  type=triang
  parameters= 0 2 2
 End_UserTerm
End_SucVariable1

RULES
 "very slow" "equal" | "stop"
 "slow" "equal" | "stop"
 "equal" "equal" | "stop"
 "fast" "equal" | "stop"
 "very fast" "equal" | "run"
 "very slow" "close" | "run"
 "slow" "close" | "run"
 "equal" "close" | "run"
 "fast" "close" | "run"
 "very fast" "close" | "run"
 "very slow" "far" | "stop"
 "slow" "far" | "stop"
 "equal" "far" | "stop"
 "fast" "far" | "stop"
 "very fast" "far" | "stop"
 "very slow" "too close" | "run"
 "slow" "too close" | "run"
 "equal" "too close" | "run"
 "fast" "too close" | "run"
 "very fast" "too close" | "run"
END_RULES

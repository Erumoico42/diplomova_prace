!COMMENT!
Enter your description of the rulebase here.
!END_COMMENT!

TypeOfDescription=linguistic
InfMethod=Fuzzy_Approximation-functional
DefuzzMethod=SimpleCenterOfGravity
UseFuzzyFilter=false

NumberOfAntecedentVariables=4
NumberOfSuccedentVariables=1
NumberOfRules=54

AntVariable1
 name=distanceB
 settings=new
 context=<0,5,10>
 discretization=301
 UserTerm
  name=too close
  type=trapezoid
  parameters= 0 0 1 4.5
 End_UserTerm
 UserTerm
  name=close
  type=trapezoid
  parameters= 1 4.5 5.5 8.5
 End_UserTerm
 UserTerm
  name=far
  type=trapezoid
  parameters= 5.5 8.5 10 10
 End_UserTerm
End_AntVariable1

AntVariable2
 name=speedB
 settings=new
 context=<0,5,7>
 discretization=301
 UserTerm
  name=too slow
  type=trapezoid
  parameters= 0 0 0.5 2
 End_UserTerm
 UserTerm
  name=slow
  type=trapezoid
  parameters= 0.5 2 4 6
 End_UserTerm
 UserTerm
  name=fast
  type=trapezoid
  parameters= 4 6 7 7
 End_UserTerm
End_AntVariable2

AntVariable3
 name=distanceA
 settings=new
 context=<0,5,10>
 discretization=301
 UserTerm
  name=too close
  type=trapezoid
  parameters= 0 0 1 3
 End_UserTerm
 UserTerm
  name=close
  type=trapezoid
  parameters= 1 3 6 8
 End_UserTerm
 UserTerm
  name=far
  type=trapezoid
  parameters= 6 8 10 10
 End_UserTerm
End_AntVariable3

AntVariable4
 name=speedA
 settings=new
 context=<0,5,7>
 discretization=301
 UserTerm
  name=slow
  type=trapezoid
  parameters= 0 0 1 5
 End_UserTerm
 UserTerm
  name=fast
  type=trapezoid
  parameters= 1 5 7 7
 End_UserTerm
End_AntVariable4

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
 "too close" "too slow" "close" "slow" | "run"
 "too close" "slow" "close" "slow" | "stop"
 "too close" "fast" "close" "slow" | "stop"
 "close" "too slow" "close" "slow" | "stop"
 "close" "slow" "close" "slow" | "stop"
 "close" "fast" "close" "slow" | "stop"
 "far" "too slow" "close" "slow" | "run"
 "far" "slow" "close" "slow" | "run"
 "far" "fast" "close" "slow" | "run"
 "too close" "too slow" "far" "slow" | "stop"
 "too close" "slow" "far" "slow" | "stop"
 "too close" "fast" "far" "slow" | "stop"
 "close" "too slow" "far" "slow" | "stop"
 "close" "slow" "far" "slow" | "stop"
 "close" "fast" "far" "slow" | "stop"
 "far" "too slow" "far" "slow" | "stop"
 "far" "slow" "far" "slow" | "stop"
 "far" "fast" "far" "slow" | "run"
 "too close" "too slow" "too close" "slow" | "run"
 "too close" "slow" "too close" "slow" | "stop"
 "too close" "fast" "too close" "slow" | "stop"
 "close" "too slow" "too close" "slow" | "run"
 "close" "slow" "too close" "slow" | "stop"
 "close" "fast" "too close" "slow" | "stop"
 "far" "too slow" "too close" "slow" | "run"
 "far" "slow" "too close" "slow" | "run"
 "far" "fast" "too close" "slow" | "run"
 "too close" "too slow" "close" "fast" | "run"
 "too close" "slow" "close" "fast" | "run"
 "too close" "fast" "close" "fast" | "stop"
 "close" "too slow" "close" "fast" | "stop"
 "close" "slow" "close" "fast" | "stop"
 "close" "fast" "close" "fast" | "stop"
 "far" "too slow" "close" "fast" | "run"
 "far" "slow" "close" "fast" | "run"
 "far" "fast" "close" "fast" | "run"
 "too close" "too slow" "far" "fast" | "stop"
 "too close" "slow" "far" "fast" | "stop"
 "too close" "fast" "far" "fast" | "stop"
 "close" "too slow" "far" "fast" | "stop"
 "close" "slow" "far" "fast" | "stop"
 "close" "fast" "far" "fast" | "run"
 "far" "too slow" "far" "fast" | "run"
 "far" "slow" "far" "fast" | "stop"
 "far" "fast" "far" "fast" | "stop"
 "too close" "too slow" "too close" "fast" | "run"
 "too close" "slow" "too close" "fast" | "stop"
 "too close" "fast" "too close" "fast" | "stop"
 "close" "too slow" "too close" "fast" | "run"
 "close" "slow" "too close" "fast" | "run"
 "close" "fast" "too close" "fast" | "run"
 "far" "too slow" "too close" "fast" | "run"
 "far" "slow" "too close" "fast" | "run"
 "far" "fast" "too close" "fast" | "run"
END_RULES
